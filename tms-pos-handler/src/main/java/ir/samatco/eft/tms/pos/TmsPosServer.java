package ir.samatco.eft.tms.pos;


import ir.samatco.eft.config.ConfigProvider;
import ir.samatco.eft.config.CuratorConfig;
import ir.samatco.eft.discovery.ServiceDetails;
import ir.samatco.eft.discovery.ServiceDiscoveryClient;
import ir.samatco.eft.discovery.ServiceName;
import ir.samatco.eft.tms.codec.Codec;
import ir.samatco.eft.tms.service.ProjectRepository;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.naming.directory.DirContext;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Date: 2/3/14 3:41 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class TmsPosServer {
    private ProjectRepository projectRepository;
    private String[] port;
    private static Hashtable environment = new Hashtable(5);
    private static DirContext ctx = null;
    private static String userDir = "ou=people";
    private static String mainPath = "dc=switch,dc=samat,dc=com";
//    private static String zookeeperHostname = "eft-zookeeper.node.internal.qezel";
    private static String zookeeperHostname = "qezel-server";
//    private static String ldapHostName= "eft-ldap.node.internal.qezel";
//    private static String ldapHostName= "qezel-server";


    private static ConfigProvider.ConfigContext tmsZookeeperContext = null;
    private static ServiceDiscoveryClient serviceDiscoveryClient = null;
    private static ConfigProvider configProvider = null;


    private Codec codec;

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public void setConfigProvider(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    public static ExecutorService workerExecutor = Executors.newCachedThreadPool();
    public static ExecutorService bossExecutor = Executors.newCachedThreadPool();

    public void run() throws Exception {

        ChannelFactory factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);

        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
//              return Channels.pipeline(new ClientMessageHandler(), new PosFrameDecoder(4,true),new ClientMessageHandler());
                return Channels.pipeline(new PosFrameDecoder(6, true), messageHandlerFactory(), new PosFrameEncoder(6));
            }
        });
        //ProjectRepository.getInstance().setCodec(new XMLCodec());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        port = tmsZookeeperContext.get("tmsPortList").getValue().split(",");
        for (String i : port) {
            bootstrap.bind(new InetSocketAddress(Integer.parseInt(i)));
        }
        System.out.println("--------------------------------------------------TMS Server Started successfully--------------------------------------------------\n\n");
    }

    private ClientMessageHandler messageHandlerFactory() {
        return new ClientMessageHandler(projectRepository);
    }


    public static void main(String[] args) throws Exception {
        ServiceDetails myService = new ServiceDetails()
                .serviceName(ServiceName.TerminalManagementService)
                .hostname(InetAddress.getLocalHost().getHostAddress())
                .logFilePath("/var/lib/tomcat7/logs/catalina.out")
                .port(2700);
        serviceDiscoveryClient = new ServiceDiscoveryClient(zookeeperHostname +":2181", myService);
        serviceDiscoveryClient.registerService(new ServiceDetails().serviceName(ServiceName.TerminalManagementService).hostname("eft-tms-pos-handler.node.internal.qezel").port(2700));
//
//        ServiceDetails ldapService = serviceDiscoveryClient.getFirstServiceDetails(ServiceName.Ldap);
//        if (ldapService == null) {
//            System.out.println("No LDAP Service Detected. Application is Closed");
//            return;
//        }

        configProvider = new CuratorConfig(zookeeperHostname);
        tmsZookeeperContext = configProvider.getContext("tms");


        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/spring/applicationContext.xml", "META-INF/spring/applicationContextNetwork.xml", "META-INF/spring/applicationContextRepository.xml");
        TmsPosServer tmsPosServer = (TmsPosServer) applicationContext.getBean("tmsPosServer");
        tmsPosServer.run();

    }

}


