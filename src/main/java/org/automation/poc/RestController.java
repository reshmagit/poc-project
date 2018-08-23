package org.automation.poc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

	@Controller
	public class RestController
	{
	  public RestController() {}
	   
	  
	  @RequestMapping(value={"/xmlToJSON"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  @ResponseBody
	  public String xmlToJSONconverter(@RequestBody String xml) throws Exception
	  {		  
	      ClassLoader loaders = getClass().getClassLoader();

		  File file = new File(loaders.getResource("/input.xml").getFile());
		  FileOutputStream fop = null;
		  try{
			  fop = new FileOutputStream(file);
			  if (!file.exists()) {
				  file.createNewFile();
			  }

			  // get the content in bytes
			  byte[] contentInBytes = xml.getBytes();
			  
			  fop.write(contentInBytes);
			  fop.flush();
			  fop.close();
		  }
		  catch (IOException e) {
			  e.printStackTrace();
		  } finally {
			  try {
				  if (fop != null) {
					  fop.close();
				  }
			  } catch (IOException e) {
				  e.printStackTrace();
			  }
		  }
		  
		  TransformerFactory factory = TransformerFactory.newInstance();
		  Source xslt = new StreamSource(loaders.getResource("/removens.xslt").getFile()); 
		  Transformer transformer = factory.newTransformer(xslt);
		
		  Source text = new StreamSource(file);
		  
		  StreamResult sr =new StreamResult(new File(loaders.getResource("/NewFile.xml").getFile())); 
		  transformer.transform(text,sr);
		  StringBuilder fileData = new StringBuilder();//Constructs a string buffer with no characters in it and the specified initial capacity
		  BufferedReader reader=null;
		  try {
			  reader = new BufferedReader(new FileReader((new File(loaders.getResource("/NewFile.xml").getFile())))); // need to change all the paths to relative path or class path
		  } catch (FileNotFoundException e) {
			  e.printStackTrace();
		  }

		  char[] buf = new char[1024];
		  int numRead = 0;
		  try {
			  while ((numRead = reader.read(buf)) != -1) {
				  String readData = String.valueOf(buf, 0, numRead);
				  fileData.append(readData);
				  buf = new char[1024];
			  }
		  } catch (IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }

		  try {
			  reader.close();
		  } catch (IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }

		  String returnStr = fileData.toString();

		  System.out.println(returnStr);

		  JSONObject obj = XML.toJSONObject(returnStr);
		  return obj.toString(); 
	  }
	  
	}
	

