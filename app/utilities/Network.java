package utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author johnmichaelreed2
 */
public class Network {

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                        continue;
                    }
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    Tester.killApplication("Didn't work");
                    return "";
                }
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++) {
                    buf.append(String.format("%02X:", mac[idx]));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
            Tester.killApplication("That didn't work");
        } // for now eat exceptions
        return "";
        /*try {
         // this is so Linux hack
         return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
         } catch (IOException exception) {
         return null;
         }*/
    }

    public static byte[] getMACAddressBytes(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                        continue;
                    }
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    Tester.killApplication("Didn't work");
                    return null;
                }
                return mac;
            }
        } catch (Exception ex) {
            Tester.killApplication("That didn't work");
        } // for now eat exceptions
        return null;
    }

    /**
     * Gets all viable, reachable, non-loopback inet addresses on this machine.
     * Useful for cases in which multiple inet addresses must be tried or
     * backups are needed.
     *
     * @return
     * @throws Exception if something goes wrong.
     */
    public static InetAddress[] getAllReachableViableNonLoopBackInetAddresses() throws Exception {

        // start with an empty list of InetAddresses.
        final ArrayList<InetAddress> ipAddresses = new ArrayList<>();
        // Add the default address first.
        {
            InetAddress defaultAddressOrNull = null;
            try {
                defaultAddressOrNull = InetAddress.getLocalHost();
                if ((defaultAddressOrNull != null) && !defaultAddressOrNull.isLoopbackAddress()) {
                    boolean isReachable = false;
                    isReachable = defaultAddressOrNull.isReachable(50);
                    if (isReachable) {
                        ipAddresses.add(defaultAddressOrNull);
                    }
                }
            } catch (Exception uhe) {
                // keep going.
            }
        }
        // Go through the network interfaces.
        List<NetworkInterface> interfaces = null;
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException se) {
            Print.exception("Failed to get any network interfaces.", se);
            ipAddresses.trimToSize();
            InetAddress[] inetAddressArr = new InetAddress[ipAddresses.size()];
            inetAddressArr = ipAddresses.toArray(inetAddressArr);
            return inetAddressArr;
        }
        for (final NetworkInterface intf : interfaces) {
            try {
                if (intf.isLoopback() || !intf.isUp()) {
                    // we are not interested in loopback addresses or non-functional interfaces.
                    continue; // keep going.
                }
            } catch (SocketException se) {
                Print.exception("Failed to access network interface " + intf.getDisplayName(), se);
                continue; // keep going.
            }
            final List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
            // First get all the site local addresses
            for (InetAddress addr : addrs) {
                if (addr == null || addr.isLoopbackAddress()) {
                    continue; // no null addresses or loopback addresses, keep going.
                } else {
                    Tester.check(addr.getHostAddress().toString().contains(":")
                                    || addr.getHostAddress().toString().contains("."),
                            "ipv6 addresses contain colons, ipv4 addresses contain periods.");
                    boolean isReachable = false;
                    try {
                        isReachable = addr.isReachable(50);
                    } catch (IOException ioe) {
                        // I couldn't reach this address.
                        continue; // keep going.
                    }
                    // Only add what is reachable, no duplicates.
                    if (isReachable && !ipAddresses.contains(addr)) {
                        ipAddresses.add(addr);
                    }
                }
            }
        }
        ipAddresses.trimToSize();
        InetAddress[] toReturn = new InetAddress[ipAddresses.size()];
        toReturn = ipAddresses.toArray(toReturn);
        return toReturn;
    }

    private static void checkIfNumLinkLocalAddressesIsReasonable(final InetAddress[] inetAddresses) {
        int numSiteLocalIpv6Addresses = 0;
        int numSiteLocalIpv4Addresses = 0;
        for (InetAddress addr : inetAddresses) {
            Tester.check(!(addr == null), "No null addresses");
            if (addr.getHostAddress().toString().contains(":")) {
                if (addr.isLinkLocalAddress()) {
                    ++numSiteLocalIpv6Addresses;
                }
            } else if (addr.getHostAddress().toString().contains(".")) {
                if (addr.isLinkLocalAddress()) {
                    ++numSiteLocalIpv4Addresses;
                }
            } else {
                Tester.killApplication("All ip addresses must contain either a colon or a dot.");
            }
        }
        Tester.check(numSiteLocalIpv4Addresses == 0 || numSiteLocalIpv4Addresses == 1, "Too many link local ipv4 addresses");
        Tester.check(numSiteLocalIpv6Addresses == 0 || numSiteLocalIpv6Addresses == 1, "Too many link local ipv6 addresses");
    }
}
