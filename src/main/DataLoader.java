package main;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.collect.Multiset.Entry;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;


public class DataLoader {
	
	public static void main(String[] args) throws IOException {
	    // Build a new authorized API client service.
		String passwordString=args[0];
		
		download_task();
		System.err.println("@beta: download tasks finished ...");
		unzip_task("benchmark/tasks/tasks.zip", passwordString,"benchmark/tasks");
		System.err.println("@beta: unzip tasks finished ...");
		HashMap<String, String> task_data=new HashMap<String, String>();
		
		loadAll_task("benchmark/tasks", task_data) ;
		System.err.println("@beta: load tasks finished ...");
		
		System.err.println("@beta: task_data: "+task_data.size());
		System.err.println("@beta: you can find the zip file at: benchmark/tasks/tasks.zip");
		download_data();
		System.err.println("@beta: download network data finished ...");
		
		unzip_data("benchmark/data/data.zip", passwordString,"benchmark/data");
		System.err.println("@beta: unzip network data finished ...");
		HashMap<String, String> network_data=new HashMap<String, String>();
		
		loadAll_data("benchmark/data", network_data) ;
		System.err.println("@beta: load network data finished ...");
		
		System.err.println("@beta: task_data: "+network_data.size());
		System.err.println("@beta: you can find the zip file at: benchmark/data/data.zip");
	}
	
	
	public static void download_task() throws IOException {
		if(!new java.io.File("benchmark/tasks/tasks.zip").exists()){
			Drive service = getDriveService();
		    // Print the names and IDs for up to 10 files.
		    String fileId = "17OriW3K1PaagUHzkSQWTmB06kV0rMVWw";
		    OutputStream outputStream = new FileOutputStream("benchmark/tasks/tasks.zip");
		    service.files().get(fileId)
		        .executeMediaAndDownloadTo(outputStream);
		    outputStream.flush();
		    outputStream.close();	
		}
	}
	
	public static void download_data() throws IOException {
		if(!new java.io.File("benchmark/data/data.zip").exists()){
			 Drive service = getDriveService();
			    // Print the names and IDs for up to 10 files.
			    String fileId = "11IZSdXXmOsl2uNUg7u9OqRr_UrKCGer3";
			    OutputStream outputStream = new FileOutputStream("benchmark/data/data.zip");
			    service.files().get(fileId)
			        .executeMediaAndDownloadTo(outputStream);
			    outputStream.flush();
			    outputStream.close();
		}
	}

	/** Application name. */
	private static final String APPLICATION_NAME = "Drive API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
	        System.getProperty("user.home"),
	        ".credentials/n/drive-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
	        .getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 * 
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/drive-java-quickstart
	 */
	private static final java.util.Collection<String> SCOPES = DriveScopes
	        .all();

	static {
	    try {
	        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	        DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
	    } catch (Throwable t) {
	        t.printStackTrace();
	        System.exit(1);
	    }
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
	    // Load client secrets.
	    InputStream in = DataLoader.class
	            .getResourceAsStream("/main/client_secret.json");
	    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
	            JSON_FACTORY, new InputStreamReader(in));

	    // Build flow and trigger user authorization request.
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
	            .setDataStoreFactory(DATA_STORE_FACTORY)
	            .setAccessType("offline").build();
	    Credential credential = new AuthorizationCodeInstalledApp(flow,
	            new LocalServerReceiver()).authorize("user");
	    System.out.println("Credentials saved to "
	            + DATA_STORE_DIR.getAbsolutePath());
	    return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	public static Drive getDriveService() throws IOException {
	    Credential credential = authorize();
	    return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
	            .setApplicationName(APPLICATION_NAME).build();
	}

	
	
	public static void unzip_task(String archive, String password, String target) throws IOException {
		
		 try {
	         ZipFile zipFile = new ZipFile(archive);
	         if (zipFile.isEncrypted()) {
	            zipFile.setPassword(password.toCharArray());
	         }
	         zipFile.extractFile("tasks/experiment.zip", target);
	    } catch (ZipException e) {
	        e.printStackTrace();
	    }
		 try {
	         ZipFile zipFile = new ZipFile(target+"/tasks/experiment.zip");
	         if (zipFile.isEncrypted()) {
	            zipFile.setPassword(password.toCharArray());
	         }
	         zipFile.extractAll( target);
	    } catch (ZipException e) {
	        e.printStackTrace();
	    }
		new java.io.File(target+"/tasks/experiment.zip").delete();
		new java.io.File(target+"/tasks").delete();
	}
	
	public static void unzip_data(String archive, String password, String target) throws IOException {
		 try {
	         ZipFile zipFile = new ZipFile(archive);
	         if (zipFile.isEncrypted()) {
	            zipFile.setPassword(password.toCharArray());
	         }
	         zipFile.extractFile("data/network.zip", target);
	    } catch (ZipException e) {
	        e.printStackTrace();
	    }
		 try {
	         ZipFile zipFile = new ZipFile(target+"/data/network.zip");
	         if (zipFile.isEncrypted()) {
	            zipFile.setPassword(password.toCharArray());
	         }
	         zipFile.extractAll( target);
	    } catch (ZipException e) {
	        e.printStackTrace();
	    }
		 new java.io.File(target+"/data/network.zip").delete();
			new java.io.File(target+"/data").delete();
	}
	
	public static void loadAll_task(String dir, HashMap<String, String> data) throws IOException {
		for(java.io.File file: new java.io.File(dir).listFiles()) {
			if (file.isDirectory()) {
				loadAll_task(file.getAbsolutePath(), data);
			}else {
				String root=new java.io.File("benchmark/tasks").getAbsolutePath();
				String path=file.getAbsolutePath();
				String true_pathString=path.replace(root, "").trim();
				data.put(true_pathString, readFile(file.getAbsolutePath()));
			}
		}
	}
	
	public static void loadAll_data(String dir, HashMap<String, String> data) throws IOException {
		for(java.io.File file: new java.io.File(dir).listFiles()) {
			if (file.isDirectory()) {
				loadAll_task(file.getAbsolutePath(), data);
			}else {
				if(!file.getName().contains(".zip")) {
					String root=new java.io.File("benchmark/data").getAbsolutePath();
					String path=file.getAbsolutePath();
					String true_pathString=path.replace(root, "").trim();
					data.put(true_pathString, readFile(file.getAbsolutePath()));	
				}
			}
		}
	}
	
	public static String readFile(String file) throws IOException {
		BufferedReader br =new BufferedReader(new FileReader(new java.io.File(file)));
		String line=null;
		StringBuffer sb=new StringBuffer();
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				sb.append(line).append("\n");
			}
		}
		br.close();
		return sb.toString().trim();
	}
}