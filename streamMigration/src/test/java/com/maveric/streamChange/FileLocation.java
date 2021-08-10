package com.maveric.streamChange;

import java.io.File;

public class FileLocation {
	
	static String inputexcel=new File("src/test/resources/input.xlsx").getAbsolutePath();
	static String outputexcel=new File("src/test/resources/output.xlsx").getAbsolutePath();
	static String DesignationFolder=new File("src/test/resources/Designation").getAbsolutePath();
	static String individualRecordsFolder=new File("src/test/resources").getAbsolutePath();

}
