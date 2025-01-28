package com.lime0x00.models.network;

import com.lime0x00.utils.Response;
import com.lime0x00.exceptions.InvalidIPAddressException;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public abstract class IPAddress {
    public enum IPVersion {
        IPV4('.', 4),
        IPV6(':', 16);
        private final char separator;
        private final int byteSize;
        IPVersion(char separator, int byteSize) {
            this.separator = separator;
            this.byteSize = byteSize;
        }
        public char getSeparator() {
            return separator;
        }
        public int getByteSize() {
            return byteSize;
        }
    }
    protected final String ipAddr;
    protected final byte[] rawBytes;
    protected final IPVersion version;
    protected static final int DEFAULT_TIMEOUT = 1000;

    public IPAddress(String ipAddr) {
        this.ipAddr = ipAddr;
        this.version = determineVersion(ipAddr);

        if (!isValidFormat(ipAddr)) {
            throw new InvalidIPAddressException("Invalid IP address format: " + ipAddr);
        }

        this.rawBytes = parseToBytes(ipAddr);
    }

    public IPAddress(byte[] rawBytes) {
        if (rawBytes.length == IPVersion.IPV4.getByteSize()) {
            this.version = IPVersion.IPV4;
        } else if (rawBytes.length == IPVersion.IPV6.getByteSize()) {
            this.version = IPVersion.IPV6;
        } else {
            throw new InvalidIPAddressException("Invalid byte length for an IP address");
        }

        if (!isValidFormat(rawBytes)) {
            throw new InvalidIPAddressException("Error This IP Address Is Not Valid");
        }

        this.rawBytes = rawBytes;
        this.ipAddr = parseToString(rawBytes);
    }
    
    public IPAddress (IPAddress other) {
        this.ipAddr = other.ipAddr;
        this.version = other.version;
        this.rawBytes = other.rawBytes.clone();
    }

    public abstract byte[] parseToBytes(String ipAddr);
    protected abstract String parseToString(byte[] rawBytes);
    protected abstract boolean isValidFormat(String ipAddr);
    protected abstract boolean isValidFormat(byte[] rawBytes);
    public abstract IPAddress plus(IPAddress other);
    public abstract int compareTo(Object other);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IPAddress other = (IPAddress) obj;
        return Arrays.equals(this.rawBytes, other.rawBytes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddr, Arrays.hashCode(rawBytes));
    }

    public Response<String> ping(int timeout) {
        try {
            InetAddress address = InetAddress.getByName(ipAddr);
            boolean reachable = address.isReachable(timeout);
            return reachable
                    ? Response.success("Ping to " + ipAddr + " successful")
                    : Response.failure("Ping to " + ipAddr + " failed");
        } catch (UnknownHostException e) {
            return Response.exception("Unknown host: " + ipAddr, e);
        } catch (Exception e) {
            return Response.exception("Ping to " + ipAddr + " encountered an error", e);
        }
    }

    public Response<String> ping() {
        return ping(DEFAULT_TIMEOUT);
    }

    public static IPVersion determineVersion (String ipAddr) {
        if (ipAddr == null || ipAddr.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        if (ipAddr.contains(IPVersion.IPV6.getSeparator() + "")) {
            return IPVersion.IPV6;
        } else if (ipAddr.contains(IPVersion.IPV4.getSeparator() + "")) {
            return IPVersion.IPV4;
        } else {
            throw new IllegalArgumentException("Invalid IP address format: " + ipAddr);
        }
    }

    @Override
    public String toString() {
        return ipAddr;
    }

    public byte[] getRawBytes() {
        return rawBytes.clone();
    }

    public IPVersion getVersion() {
        return version;
    }
}
