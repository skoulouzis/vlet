package test;
///* (C) VL-e 
// * 
// * See: http://www.vl-e.nl 
// * 
// * Created on Jul 31, 2006
// * Author: P.T. de Boer 
// */
//
//package test.misc;
//
//import nl.uva.vlet.Global;
//import nl.uva.vlet.data.VAttribute;
//import nl.uva.vlet.data.VAttributeSet;
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.vfs.VDir;
//import nl.uva.vlet.vfs.VFSClient;
//import nl.uva.vlet.vfs.srbfs.SrbQuery;
//import nl.uva.vlet.vfs.srbfs.SrbFileSystem;
//import nl.uva.vlet.vfs.srbfs.SrbDir;
//import edu.sdsc.grid.io.srb.SRBFileSystem;
//import edu.sdsc.grid.io.srb.SRBMetaDataSet;
//
//public class testSRBQuery
//{
//
//    private static VFSClient vfs;
//
//    static String srbhome = "srb://piter.de.boer.vlenl@srb.grid.sara.nl:50000/VLENL/home/piter.de.boer.vlenl";
//
//    public static void main(String args[])
//    {
//        vfs = new VFSClient();
//
//        Global.setDebug(true);
//
//        testQueryDirs();
//
//        // testGetUserDomain();
//        // testSimpleQuery();
//    }
//
//    private static void testQueryDirs()
//    {
//
//        try
//        {
//            VDir dir;
//            dir = vfs.getDir(srbhome);
//            if (dir.exists() == false)
//                Global.errorPrintln("testACL", "Directory doesn't exists:" + dir);
//
//            SRBFileSystem srbfs = ((SrbDir) dir).getSRBFileSystem();
//
//            String names[] = null;
//
//            VAttributeSet[] attrs = nl.uva.vlet.vfs.srbfs.SrbQuery.queryDir(srbfs, dir.getPath(), names);
//            int index = 0;
//
//            Message("Querying:" + dir);
//
//            for (VAttributeSet set : attrs)
//            {
//                System.out.println("set:" + (index++) + ":" + set);
//            }
//            Message("Querying: non existing dir");
//            attrs = nl.uva.vlet.vfs.srbfs.SrbQuery.queryDir(srbfs, "/nonexisting", names);
//
//            for (VAttributeSet set : attrs)
//            {
//                System.out.println("set:" + (index++) + ":" + set);
//            }
//
//        }
//        catch (VlException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
//
//    private static void Message(String msg)
//    {
//        System.out.println(msg);
//    }
//
//    private static void testGetUserDomain()
//    {
//        try
//        {
//
//            VDir dir = vfs.getDir("srb://piter.de.boer.vlenl@mu2.matrix.sara.nl:50000/VLENL/home/piter.de.boer.vlenl");
//
//            if (dir.exists() == false)
//                Global.errorPrintln("testACL", "Directory doesn't exists:" + dir);
//
//            SRBFileSystem srbfs = ((SrbDir) dir).getSRBFileSystem();
//
//            VAttribute attr = SrbFileSystem.getUserDomainAttribute(srbfs, "piter.de.boer");
//
//            System.out.println("user domain is=" + attr);
//
//        }
//        catch (VlException e)
//        {
//            System.out.println("***Error: Exception:" + e);
//            e.printStackTrace();
//        }
//    }
//
//    public static void testSimpleQuery()
//    {
//        try
//        {
//
//            VDir dir = vfs.getDir("srb://piter.de.boer.vlenl@mu2.matrix.sara.nl:50000/VLENL/home/piter.de.boer.vlenl");
//
//            if (dir.exists() == false)
//                Global.errorPrintln("testACL", "Directory doesn't exists:" + dir);
//
//            String fields[] = new String[2];
//            fields[0] = SRBMetaDataSet.USER_NAME;
//            fields[1] = SRBMetaDataSet.USER_DOMAIN;
//
//            VAttributeSet[] Sets = SrbQuery.simpleQuery(((SrbDir) dir).getSRBFileSystem(), fields);
//
//        }
//        catch (VlException e)
//        {
//            System.out.println("***Error: Exception:" + e);
//            e.printStackTrace();
//        }
//
//    }
//}
