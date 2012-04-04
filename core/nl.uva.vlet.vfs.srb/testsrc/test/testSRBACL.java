package test;


import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import edu.sdsc.grid.io.srb.SRBMetaDataSet;

public class testSRBACL
{
    public static String srbpref = "srb://piter.de.boer.vlenl@srb.grid.sara.nl:50000";

    public static void main(String args[])
    {
        Global.setDebug(true);

        try
        {
            VFSClient vfs = new VFSClient();

            VDir dir = vfs.getDir(srbpref + "/VLENL/home/vlenl.groups");

            if (dir.exists() == false)
                Global.errorPrintln("testACL", "Directory doesn't exists:" + dir);

            VAttribute[][] acl1 = dir.getACL();

            // project dir

            dir = vfs.getDir(srbpref + "/VLENL/home/vlenl.groups");

            if (dir.exists() == false)
                Global.errorPrintln("testACL", "Directory doesn't exists:" + dir);

            System.out.println("Fetching acl of:" + dir);
            acl1 = dir.getACL();

            if (acl1 == null)
            {
                Global.errorPrintln("testACL", "getACL() returned NULL:" + dir);
            }
            else
            {
                int index = 0;

                for (VAttribute aclRecord[] : acl1)
                {
                    for (VAttribute attr : aclRecord)
                    {
                        System.out.println("Directory aclRecord[" + index + "]=" + attr);
                        index++;
                    }
                }
            }

            VDir remoteDir = vfs.getDir(srbpref + "/VLENL/home/piter.de.boer.vlenl");

            VFile file = remoteDir.createFile("testFile", true);

            if (file.exists() == false)
                Global.errorPrintln("testACL", "Test file doesn't exists:" + file);

            System.out.println("Fetching acl of:" + file);
            acl1 = file.getACL();

            acl1[0][2].setValue("read");

            VAttribute entity = new VAttribute(SRBMetaDataSet.USER_NAME, "abtroun");

            VAttribute aclRecord[] = file.createACLRecord(entity, false);

            int index = 0;

            aclRecord[2].setValue("annotate");

            for (VAttribute attr : aclRecord)
            {
                System.out.println("File aclRecord[" + index + "]=" + attr);
                index++;
            }

            VAttribute acl2[][] = new VAttribute[2][];
            acl2[0] = acl1[0]; // copy piter.de.boer's ACL
            acl2[1] = aclRecord;

            file.setACL(acl2);
            file.getACL();

            aclRecord[2].setValue("none");
            file.setACL(acl2);

            // 2d entry should have gone !

            file.getACL();

        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }
}
