package simsimjavautilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class createVerbsJsonFile {

	
		// TODO Auto-generated method stub
		private static final String sourcePath = "c:\\italian-verbs-prova\\content\\";
		private static final String destinationPath = "c:\\italian-verbs-prova\\conjugation.json";
		public static void main(String[] args)  {
			try {
				//
	            FileOutputStream f = new FileOutputStream(destinationPath, true);
	            PrintWriter pw = new PrintWriter(f);
	            pw.println("[");
	            pw.flush();
	            pw.close();
	            f.close();
	            //
	            int countVerbsToLoad = countVerbsToLoad();
				readVerbsToLoad(countVerbsToLoad);
				//
	            FileOutputStream ff = new FileOutputStream(destinationPath, true);
	            PrintWriter pwf = new PrintWriter(ff);
	            pwf.println("]");
	            pwf.flush();
	            pwf.close();
	            ff.close();
	            //
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}
		//
		public static int countVerbsToLoad() throws IOException, ParseException {
		//
            JSONParser jsonParser = new JSONParser();
			JSONArray a = (JSONArray) jsonParser.parse
					(new FileReader("c:\\italian-verbs-prova\\verbstoload.json"));

			int countVerbsToLoad = 0;
			for (Object o : a)
			  {
				  
				countVerbsToLoad++;

			    
			  }
			System.out.println("countVerbToLoad=" + countVerbsToLoad );
		//	
			return countVerbsToLoad;
		}
		//		
		//
		public static void readVerbsToLoad(int countVerbsToLoad) throws IOException, ParseException {
		//
            JSONParser jsonParser = new JSONParser();
			JSONArray a = (JSONArray) jsonParser.parse
					(new FileReader("c:\\italian-verbs-prova\\verbstoload.json"));
			
			int i = 0;

			for (Object o : a)
			  {
				String verbToLoad = (String) o;  
			    // JSONObject verbToLoad = (JSONObject) o;

				writeConiugation(verbToLoad);
				// non scrivo l'ultima virgola
				i++;
				if (i < countVerbsToLoad) {
	            FileOutputStream f = new FileOutputStream(destinationPath, true);
	            PrintWriter pw = new PrintWriter(f);
	            pw.println(",");
	            pw.flush();
	            pw.close();
	            f.close();
				}
	            //

			    
			  }
		//	
		}
		//
		//
		public static void writeConiugation(String verbToLoad) throws IOException, ParseException {
		//
		//	System.out.println(System.getProperty("user.dir"));
			FileInputStream sourceStream = new FileInputStream(sourcePath+verbToLoad+".json");
			BufferedInputStream sourceBufferStream = new BufferedInputStream(sourceStream);
			FileOutputStream destStream = new FileOutputStream(destinationPath, true);
			BufferedOutputStream destBufferStream = new BufferedOutputStream(destStream);
			byte[] bytes = new byte[1000];
			int bytesRead=0;
			while ((bytesRead=sourceBufferStream.read(bytes)) != -1) {
				destBufferStream.write(bytes,0,bytesRead);
			}
			sourceBufferStream.close();
			destBufferStream.close();
			destStream.close();
			sourceStream.close();

			}

	}


