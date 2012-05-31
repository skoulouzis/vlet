///*
// * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
// * 
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache Licence at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * See: http://www.vl-e.nl/ 
// * See: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// * $Id: TestGUI.java,v 1.2 2011-05-02 13:28:46 ptdeboer Exp $  
// * $Date: 2011-05-02 13:28:46 $
// */ 
//// source: 
//
//package test.gui;
//
//import java.util.Iterator;
//import java.util.List;
//
//import javax.swing.JButton;
//import javax.swing.JTextField;
//
//import junit.extensions.jfcunit.JFCTestCase;
//import junit.extensions.jfcunit.JFCTestHelper;
//import junit.extensions.jfcunit.RobotTestHelper;
//import junit.extensions.jfcunit.eventdata.AbstractMouseEventData;
//import junit.extensions.jfcunit.eventdata.MouseEventData;
//import junit.extensions.jfcunit.eventdata.StringEventData;
//import junit.extensions.jfcunit.finder.ComponentFinder;
//import nl.uva.vlet.gui.editors.ResourceEditor;
//import nl.uva.vlet.gui.panels.fields.AttrParameterField;
//
//public class TestGUI extends JFCTestCase
//{
//    private ResourceEditor editor;
//
//    public TestGUI(String name)
//    {
//        super(name);
//    }
//
//    protected void setUp() throws Exception
//    {
//        super.setUp();
//
//        // Choose the text Helper
//        setHelper(new JFCTestHelper()); // Uses the AWT Event Queue.
//        setHelper(new RobotTestHelper()); // Uses the OS Event Queue.
//
//        // UIGlobal.init();
//        // ProxyTNode.init();
//        //
//        // VRSContext context = VRSContext.getDefault();
//        // LogicalResourceNode node = null;
//        // VRL lfcVRL = new VRL("lfn://lfc.grid.sara.nl:5010/grid/");
//        // node = LinkNode.createServerNode(context, new VRL("myvle:/0"),
//        // lfcVRL);
//        // ProxyNode pnode = ((ProxyTNodeFactory)
//        // ProxyNode.getProxyNodeFactory()).createFrom(node);
//        // ResourceEditor.editProperties(pnode, true);
//
//        editor = new ResourceEditor();
//
//        // loginScreen = new LoginScreen("LoginScreenTest: " + getName());
//        // loginScreen.setVisible(true);
//        editor.setVisible(true);
//    }
//
//    protected void tearDown() throws Exception
//    {
//        editor = null;
//        // getHelper.cleanUp(this);
//        super.tearDown();
//    }
//
//    public void test1()
//    {
//
//        junit.extensions.jfcunit.finder.ComponentFinder finder = new ComponentFinder(JButton.class);
//        List allComponents = finder.findAll(editor);
//
//        Iterator iter = allComponents.iterator();
//        
//        JButton applayButton;
//        JButton resetButton;
//        JButton cancelButton;
//        JButton okButton;
//
//        JButton applyButton = null;
//        while (iter.hasNext())
//        {
//            Object comp = iter.next();
//            if (comp instanceof JButton)
//            {
//                JButton button = (JButton) comp;
//                String text = button.getText();
//                if(text.equals("Apply")){
//                    applayButton = button;
//                }
//                if(text.equals("Cancel")){
//                    cancelButton = button;
//                }
//                if(text.equals("Reset")){
//                    applayButton = button;
//                }
//                if(text.equals("OK")){
//                    okButton = button;
//                }
//            }
//        }
//        
//        assertNotNull("Could not find the Apply button", applyButton);
//        assertNotNull("Could not find the Apply button", applyButton);
////        assertNotNull("Could not find the Apply button", cancelButton);
//
//        finder = new ComponentFinder(JTextField.class);
//        allComponents = finder.findAll(editor);
//
//        iter = allComponents.iterator();
//
//        while (iter.hasNext())
//        {
//            Object comp = iter.next();
//            // System.err.println("Class name:  " + comp.getClass().getName());
//            if (comp instanceof nl.uva.vlet.gui.panels.fields.AttrParameterField)
//            {
//                AttrParameterField button = (AttrParameterField) comp;
//                String text = button.getText();
//                System.err.println("ComponentFinder:  " + button.getName());
//
//                if (text.equals("<HOSTNAME>"))
//                {
//                    button.setText("");
////                    getHelper().sendString(new StringEventData(this, button, "^?^?^?^?"));
//                    getHelper().sendString(new StringEventData(this, button, "lfc.grid.sara.nl"));
//
//                    AbstractMouseEventData evtData = new MouseEventData(this, applyButton);
//                    getHelper().enterClickAndLeave(evtData);
//
//                }
//            }
//        }
//
//        pauseAWT();
//
//    }
//
//}
