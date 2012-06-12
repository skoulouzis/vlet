/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: ViewerX509.java,v 1.9 2011-05-20 12:35:58 ptdeboer Exp $  
 * $Date: 2011-05-20 12:35:58 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.x509viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Vector;
import java.util.regex.Pattern;

import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.net.ssl.CertificateStore;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

public class ViewerX509 extends ViewerPlugin implements CertPanelListener
{
    private static final long serialVersionUID = 5397354476414010762L;
    
    public static final String ADD_METHOD="addCert";
    
    public static final String VIEW_METHOD="viewCert"; 
    
    private static String mimeTypes[] = 
            {
                    "application/x-x509-ca-cert",
                    // .crt and .pem can be both user AND CA
                    "application/x-pem-file", 
                    "application/x-x509-pem-file", 
                    "application/x-x509-crt-file", 
                    // "application/x-x509-user-cert" = user cert! (not CA) 
            };
    
    // ========================================================================
    //
    // ========================================================================
    
    private X509Certificate cert;

    private CertPanel caPanel;

    public ViewerX509()
    {
        super();
    }

    @Override
    public void disposeViewer()
    {

    }

    @Override
    public String[] getMimeTypes()
    {
        return mimeTypes;
    }

    @Override
    public String getName()
    {
        return "ViewerX509";
    }

    @Override
    public void initViewer()
    {
        initGUI();
    }

    public void initGUI()
    {
        this.setLayout(new BorderLayout());
        
        this.caPanel = new CertPanel();        
        this.caPanel.setCertPanelListener(this);
        this.add(caPanel,BorderLayout.CENTER);
        Dimension preferredSize = new Dimension(800, 350);
        this.setPreferredSize(preferredSize);
    }

    private void addCert(X509Certificate cert2, boolean save) throws Exception
    {
        //forward to ConfigManager! 
        ProxyVRSClient.getInstance().getConfigManager().addCACertificate(cert2,save); 
    }

    @Override
    public void stopViewer()
    {

    }
    
    public Vector<ActionMenuMapping> getActionMappings()
    {
        ActionMenuMapping addMapping=new ActionMenuMapping(ADD_METHOD, "Add Certificate","certs");
        ActionMenuMapping viewMapping=new ActionMenuMapping(VIEW_METHOD, "View Certificate","certs");

        // '/' is not a RE character
        
        Pattern txtPatterns[]=new Pattern[mimeTypes.length];
        
        for (int i=0;i<mimeTypes.length;i++)
        {
            txtPatterns[i]=Pattern.compile(mimeTypes[i]); 
        }
        
        addMapping.addMimeTypeMapping(txtPatterns);
        viewMapping.addMimeTypeMapping(txtPatterns);

        Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
        mappings.add(addMapping);
        mappings.add(viewMapping);
        
        return mappings; 
    }
    
    
    @Override
    public void startViewer(VRL location) throws VlException
    {
        startViewer(location,null,null);
    }
    
    @Override
    public void startViewer(VRL location, String optMethodName, ActionContext actionContext) 
    {
        //default to true ?
        boolean add=true;
        
        if (StringUtil.equals(optMethodName,VIEW_METHOD))
            add=false;
        
        if (StringUtil.equals(optMethodName,ADD_METHOD))
            add=true; 
                
        askCertificate(location,add);  
    }   
    
    @Override
    public void updateLocation(VRL loc) throws VlException
    {
        askCertificate(loc,true);
    }
    
    public void askCertificate(VRL loc,boolean askToAdd) 
    {
        try
        {
            cert = instCert(loc);

            String keyIssuers = cert.getIssuerDN().getName();
            String[] mtmp = keyIssuers.split("CN=");

            int index = mtmp[1].indexOf(",");

            keyIssuers = mtmp[1].substring(0, index);

            StringBuffer message = new StringBuffer();

            message.append("Certificate Details:\n");
            message.append("Issuer DN:              " + cert.getIssuerDN() + "\n");
            message.append("Subject DN:             " + cert.getSubjectDN() + "\n");
            message.append("Issuer X500 Principal:  " + cert.getIssuerX500Principal() + "\n");
            message.append("Signature Algorithm:    " + cert.getSigAlgName() + "\n");
            message.append("Type:                   " + cert.getType() + "\n");
            message.append("Version:                " + cert.getVersion() + "\n");
            message.append("Not After:              " + cert.getNotAfter() + "\n");
            message.append("Not Before:             " + cert.getNotBefore() + "\n");
            message.append("Serial Number:          " + cert.getSerialNumber() + "\n");

            if (askToAdd)
            {
                caPanel.setQuestion("You have been asked to trust a new Certificate Authority(CA).\n"
                        + "Accept certificate from '" + keyIssuers + "'?");
            }
            else
            {
                caPanel.setQuestion("Viewing Certificate information."); 
                caPanel.setViewOnly(true); 
            }
            
            caPanel.setMessageText(message.toString());

        }
        catch (Exception e)
        {
            caPanel.setMessageText(e.getMessage());
            caPanel.setQuestion("Exception occured"); 
            handle(e); 
        }
    }

    public boolean getAlwaysStartStandalone()
    {
        return true;
    }

    private X509Certificate instCert(VRL loc) throws Exception
    {
        VNode vnode = getVNode(loc);
        String txt = UIGlobal.getResourceLoader().getText(vnode, textEncoding);

        // Use hardcoded String to find start of certificate. 
        // Current Pem reader is just as simplistic.  
        int index = txt.indexOf("-----BEGIN CERTIFICATE");

        if (index >= 0)
        {
            // Get (Expected) DER part
            String derStr = txt.substring(index);
            return CertificateStore.createDERCertificateFromString(derStr);
        }
        int len=txt.length(); 
        if (len>80)
            len=80; 
        throw new IOException("Couldn't find start of (DER) certificate!\n---\nStarting text:\n" +'"' + txt.substring(0,len)+'"' );

    }

    public void optionSelected()
    {
        int opt = caPanel.getOption();
        try
        {
            if (opt == CertPanel.OK)
            {
                addCert(cert, true);
            }
            if (opt == CertPanel.TEMPORARY)
            {
                addCert(cert, false);
            }
        }
        catch (Exception e)
        {
            UIGlobal.showException(e);
        }
        disposeJFrame();
    }

    /**
     * This method should return an instance of this class which does NOT
     * initialize it's GUI elements. This method is ONLY required by Jigloo if
     * the superclass of this class is abstract or non-public. It is not needed
     * in any other situation.
     */
    public static Object getGUIBuilderInstance()
    {
        return new ViewerX509(Boolean.FALSE);
    }

    /**
     * This constructor is used by the getGUIBuilderInstance method to provide
     * an instance of this class which has not had it's GUI elements initialized
     * (ie, initGUI is not called in this constructor).
     */
    public ViewerX509(Boolean initGUI)
    {
        super();
    }
    
  
}
