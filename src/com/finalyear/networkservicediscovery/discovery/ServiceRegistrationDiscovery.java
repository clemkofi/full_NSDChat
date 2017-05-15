import com.finalyear.networkservicediscovery.pojos.ListServiceDescription;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Kofi on 17/04/2017.
 */
public class ServiceRegistrationDiscovery {

    //Vector to store the list of services identified
    static Vector<ListServiceDescription> descriptors;

//    static List<listServiceDescription> descriptors;
    public static final String SERVICE_NAME = "Toshiba";
    public static final String SERVICE_TYPE = "_NsdChat._tcp.local.";

    private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {

            if(!(event.getInfo().getName().equals(SERVICE_NAME))) {
                System.out.println("Service added: " + event.getInfo());
            }
//            System.out.println(event.getInfo().getName());



//            //if it is not empty then clear old values
//            if(serviceIdentified != null){
//                serviceIdentified.clearAll();
//            }



        }

        @Override
        public void serviceRemoved(ServiceEvent event) {

            System.out.println("Service removed: " + event.getInfo());
            ListServiceDescription serviceRemoval = new ListServiceDescription();
            serviceRemoval.setInstanceName(event.getInfo().getName());
            serviceRemoval.setAddress(event.getInfo().getAddress());
            serviceRemoval.setPort(event.getInfo().getPort());

            if (descriptors.size() > 0) {
                System.out.println("======= Users on Network =======");

                //display all the available services
                for (ListServiceDescription descriptor : descriptors) {

                    if(descriptor.getInstanceName().equals(serviceRemoval.getInstanceName())){
                        int pos = descriptors.indexOf(descriptor);
                        System.out.println("Service at " + String.valueOf(pos) + " Removed");
                        if(pos>-1){
                            descriptors.removeElementAt(pos);
                        }
                    }

                    System.out.println(descriptor.toString());
                }
            } else {
                System.out.println("\n---NO Users FOUND---");
            }

//            boolean removePosition = descriptors.contains(serviceRemoval);
        }

        @Override
        public void serviceResolved(ServiceEvent event) {

            System.out.println("Service resolved: " + event.getInfo());
//            descriptors.add(serviceIdentified);

            //a new instance of the listServiceDescription to keep the service that is identified
            ListServiceDescription serviceIdentified;

            serviceIdentified = new ListServiceDescription(event.getInfo().getName(), event.getInfo().getPort(), event.getInfo().getAddress());

            System.out.println(serviceIdentified.getAddress() + " " + serviceIdentified.getInstanceName() + " " + serviceIdentified.getPort());

            //get a list all the services on the network and add them to the vector list of services
            if(!(serviceIdentified.getInstanceName().equals(SERVICE_NAME))){
                descriptors.add(serviceIdentified);
            }

            if (descriptors.size() > 0) {
                System.out.println("======= Users on Network =======");

                //display all the available services
                for (ListServiceDescription descriptor : descriptors) {
                    System.out.println(descriptor.toString());
                }
            } else {
                System.out.println("\n---NO Users FOUND---");
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {

        try {

            descriptors = new Vector<>();
//            descriptors = new ArrayList<>();

            registerService();
            discoverServices();

            if (descriptors.size() > 0) {
                //ignore
            } else {
                System.out.println("\n---NO Users FOUND---");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void registerService(){

        try {
            ///set mac IP to IPv4
//            setForMac setIPv4 = new setForMac();
//            setIPv4.setToIPv4();

            //creating new socket with random free port
            ServerSocket serviceSocket = new ServerSocket();
            serviceSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(),0));

            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Register a service
            ServiceInfo serviceInfo = ServiceInfo.create(SERVICE_TYPE, SERVICE_NAME, serviceSocket.getLocalPort(), "path=index.html");
            System.out.println("Service created");
            jmdns.registerService(serviceInfo);
            System.out.println("Service registered");
            System.out.println("Service-> name:" + serviceInfo.getName() + " IP:" +  InetAddress.getLocalHost().getHostAddress() + " Port:" + serviceInfo.getPort());

            // Wait a bit
            Thread.sleep(5000);

            // Unregister all services
    //            jmdns.unregisterAllServices();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException intE){
            System.out.println(intE.getMessage());
        }

    }

    public static void discoverServices(){
        try {

            //discovering services on network
            System.out.println("discovering services on network......");

            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener(SERVICE_TYPE, new SampleListener());

            // Wait a bit
            Thread.sleep(3000);

            System.out.println("Working ..... ");

        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException intE){
            System.out.println(intE.getMessage());
        }
    }

}
