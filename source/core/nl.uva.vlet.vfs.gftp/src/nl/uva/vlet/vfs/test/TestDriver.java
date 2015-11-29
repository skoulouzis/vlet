/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.vlet.vfs.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.io.CircularStreamBufferTransferer;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.vlet.uva.grid.globus.GlobusUtil;

/**
 *
 * @author S. Koulouzis
 */
public class TestDriver {

    public static void main(String args[]) {
        try {
            test1();
        } catch (Exception ex) {
            Logger.getLogger(TestDriver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            VRS.exit();
        }
    }

    private static void test1() throws Exception {
        VFSClient vfsClient = new VFSClient();
        VRSContext context = vfsClient.getVRSContext();
        context.setProperty(GlobalConfig.TCP_CONNECTION_TIMEOUT, "20000");
        VRL vrl = new VRL("gsiftp://172.17.0.2/root/");
        //Bug in sftp: We have to put the username in the url

        initGridProxy("vo", "pass", context, true);
        VFSNode[] nodes = vfsClient.list(vrl);
        for (VFSNode n : nodes) {
            System.err.println("Node: " + n.getVRL());
        }
    }

    public static void InitGlobalVFS() throws Exception {
//        if (!GlobalConfig.isGlobalInitialized()) {
             if (VRS.getRegistry().getVRSFactoryClass(nl.uva.vlet.vfs.gftp.GftpFSFactory.class.getName()) == null) {
                VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.gftp.GftpFSFactory.class);
            }
             
            copyVomsAndCerts();
            try {
                GlobalConfig.setBaseLocation(new URL("http://dummy/url"));
            } catch (MalformedURLException ex) {
                Logger.getLogger(TestDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
            // runtime configuration
            GlobalConfig.setHasUI(false);
//        GlobalConfig.setIsApplet(true);
            GlobalConfig.setPassiveMode(true);
            GlobalConfig.setIsService(true);
            GlobalConfig.setInitURLStreamFactory(false);
            GlobalConfig.setAllowUserInteraction(false);
            GlobalConfig.setUserHomeLocation(new URL("file:///" + System.getProperty("user.home")));
            GlobalConfig.setSystemProperty("grid.proxy.location", "/tmp/lobcder_proxy");
            GlobalConfig.setSystemProperty("grid.certificate.location", Global.getUserHome() + "/.globus");
            GlobalConfig.setSystemProperty("grid.proxy.lifetime", "100");
//        GlobalConfig.setUsePersistantUserConfiguration(false);
            GlobalConfig.setCACertificateLocations(System.getProperty("user.home") + "/.globus/certificates/");
            Global.init();
//        }
    }

    private static void copyVomsAndCerts() throws FileNotFoundException, VlException, URISyntaxException {
        File f = new File(System.getProperty("user.home") + "/.globus/vomsdir");
        File vomsFile = new File(f.getAbsoluteFile() + "/voms.xml");
        if (!vomsFile.exists()) {
            f.mkdirs();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream in = classLoader.getResourceAsStream("/voms.xml");
            FileOutputStream out = new FileOutputStream(vomsFile);
            CircularStreamBufferTransferer cBuff = new CircularStreamBufferTransferer((1024), in, out);
            cBuff.startTransfer(new Long(-1));
        }
        f = new File(System.getProperty("user.home") + "/.globus/certificates/");
        if (!f.exists() || f.list().length == 0) {
            f.mkdirs();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL res = classLoader.getResource("/certs/");
            File sourceCerts = new File(res.toURI());
            File[] certs = sourceCerts.listFiles();
            for (File src : certs) {
                FileInputStream in = new FileInputStream(src);
                FileOutputStream out = new FileOutputStream(f.getAbsoluteFile() + "/" + src.getName());
                CircularStreamBufferTransferer cBuff = new CircularStreamBufferTransferer((1024), in, out);
                cBuff.startTransfer(new Long(-1));
            }
        }
    }

    public static void initGridProxy(String vo, String password, VRSContext context, boolean destroyCert) throws Exception {
        InitGlobalVFS();
        if (context == null) {
            context = new VFSClient().getVRSContext();
//            context = VRS.getDefaultVRSContext();
        }

        GlobusUtil.init();

        GridProxy gridProxy = context.getGridProxy();
        if (destroyCert) {
            gridProxy.destroy();
            gridProxy = null;
        }
        if (gridProxy == null || gridProxy.isValid() == false) {
            context.setProperty("grid.proxy.location", "/tmp/lobcder_proxy");
//            context.setProperty("grid.certificate.location", Global.getUserHome() + "/.globus");
//            context.setProperty("grid.proxy.lifetime", "100");
//            context.setProperty("grid.proxy.voName", vo);
            gridProxy = context.getGridProxy();
//            if (gridProxy.isValid() == false) {
//            gridProxy.setEnableVOMS(true);
//            gridProxy.setDefaultVOName(vo);
            gridProxy.createWithPassword(password);
            if (gridProxy.isValid() == false) {
                throw new VlException("Created Proxy is not Valid!");
//                }
            }
        }
        if (!new File("/tmp/lobcder_proxy").exists()) {
            gridProxy.saveProxyTo("/tmp/lobcder_proxy");
        }
    }
}
