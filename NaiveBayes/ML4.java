import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class ML4 
{
	static int filescount[]= new int[10];
	static double prior[];
	static int tot_filesCount = 0;
	static int directoryCount_train=0;
	static int uniquewords_full_dir=0;
	static TreeMap<String, TreeMap<String, Integer>> SubDirectorytokens=  new TreeMap<String, TreeMap<String, Integer>>();
	static List<String> SubDirNames=new ArrayList<>();
	static TreeMap<String, Integer> tokens_temp,temp,temp1,term_tf_testDoc;
	static List<String> complete_tokens=new ArrayList<>();
	static TreeMap<String, TreeMap<String, Integer>> tf_each_doc_test_data=  new TreeMap<String, TreeMap<String, Integer>>();
	static TreeMap<String, TreeMap<String, Double>> likelihood_All_Terms_in_All_SubDirectories=  new TreeMap<String, TreeMap<String, Double>>();
	static TreeMap<String, Double> temp_likelihood,term_likelihood;
	static TreeMap<String, Integer> ActualSubDirCount=new TreeMap<String, Integer>();
	static TreeMap<String, Integer> CalculateSubDirCount=new TreeMap<String, Integer>();
	public static void calculatePosterior()
	{
		//posterior p(c/d5) Log(prior) +(sum of conditional probabilities of all terms in the doc w.r.t directory
		//calculating posterior of each subdir w.r.t docs
		//System.out.println("tf_each_doc_test_data"+tf_each_doc_test_data);
		    Set set = tf_each_doc_test_data.entrySet();
	        Iterator it = set.iterator();   
	        while(it.hasNext()) 
	        {
	        	//storing posterior val and dir name
	        	TreeMap<Double,String> posterior_all_Dir = new TreeMap<Double,String>();
	        	//posterior_all_Dir.
	        	Map.Entry me = (Map.Entry)it.next();
	        	String docname=me.getKey().toString(); 
	        	for(int i=0;i<SubDirNames.size();i++)
	    		{
	        		double sum=0;
	        		term_tf_testDoc= new TreeMap<String, Integer>();
	        		term_tf_testDoc=tf_each_doc_test_data.get(me.getKey());
	        		Set set2 = term_tf_testDoc.entrySet();
	        		Iterator it2 = set2.iterator();   
	        		while(it2.hasNext()) 
	        		{
	        			Map.Entry me2 = (Map.Entry)it2.next();
	        			String term=me2.getKey().toString();
	        			int freq=Integer.parseInt(me2.getValue().toString());
	        			double lh_term=(double)(findLikelihood(SubDirNames.get(i),term));
	        			sum=(double)(sum+(freq*lh_term));
	        		}
	        		double p=prior[i]+sum;
	        		posterior_all_Dir.put(p, SubDirNames.get(i));
	    		}
	        	if(!CalculateSubDirCount.containsKey(posterior_all_Dir.get(posterior_all_Dir.lastKey())))
	        		CalculateSubDirCount.put(posterior_all_Dir.get(posterior_all_Dir.lastKey()), 1);
	        	else
	        		CalculateSubDirCount.put(posterior_all_Dir.get(posterior_all_Dir.lastKey()),CalculateSubDirCount.get(posterior_all_Dir.get(posterior_all_Dir.lastKey()))+1 ); 	
		}
	}
	
	public static double findLikelihood(String dir,String term)
	{
		Set set = likelihood_All_Terms_in_All_SubDirectories.entrySet();
        Iterator it = set.iterator();  
        double lh=0;
        while(it.hasNext()) 
        {
        	Map.Entry me = (Map.Entry)it.next();
        	if(dir.contains(me.getKey().toString()))
        	{
        		term_likelihood=new TreeMap<String, Double>();
        		term_likelihood=likelihood_All_Terms_in_All_SubDirectories.get(dir);
        		if(term_likelihood.containsKey(term))
        		lh=term_likelihood.get(term);
        	}
        }
		return lh;
	}
	
	public static void calculateAccuracy()
	{
		int trulyClassified_count=0;
		int total=0;
		//System.out.println("CalculateSubDirCount"+CalculateSubDirCount);
		//System.out.println("ActualSubDirCount"+ActualSubDirCount);
		Set set1 = CalculateSubDirCount.entrySet();
		Set set2 = ActualSubDirCount.entrySet();
		Iterator it1 = set1.iterator(); 
		 while(it1.hasNext()) 
	        {
	        	Map.Entry me = (Map.Entry)it1.next();
	        	total=total+Integer.parseInt(me.getValue().toString());
	        }
        Iterator it2 = set2.iterator();  
        while(it2.hasNext()) 
        {
        	Map.Entry me = (Map.Entry)it2.next();
        	//System.out.println("me.getKey()"+me.getKey());
        	if(CalculateSubDirCount.containsKey(me.getKey().toString()))
        	{
        	if(ActualSubDirCount.get(me.getKey())>CalculateSubDirCount.get(me.getKey()))
        		trulyClassified_count=trulyClassified_count+CalculateSubDirCount.get(me.getKey());
        	else
        		trulyClassified_count=trulyClassified_count+ActualSubDirCount.get(me.getKey());
        	}
        }
        //System.out.println("trulyClassified_count: "+trulyClassified_count);
        //System.out.println("total: "+total);
        double Accuracy=(double)trulyClassified_count/total;
        Accuracy=100*Accuracy;
        System.out.println("***************************************************************");
		System.out.println("Test Accuracy: "+Accuracy);
	}

	public static void main(String[] args) 
	{
		String trainDatafilePath = args[0].toString();
		String testDatafilePath = args[1].toString();
		try 
		{
		Files(trainDatafilePath,"train");
		Files(testDatafilePath,"test");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		calculate_prior();
		//find V: unique words in entire dir.
		uniquewords_full_dir=complete_tokens.size();
		calculate_likelihood_terms_each_Subdirectory();
		calculatePosterior();
		calculateAccuracy();
	}
	
	public static void calculate_prior()
	{
		prior=new double[SubDirNames.size()+1];
		for(int j=0;j<directoryCount_train;j++)
		{
			prior[j]= (double)((double)filescount[j]/(double)tot_filesCount);
			prior[j]=(double)Math.log10(prior[j]);
		}
	}
	
	public static void calculate_likelihood_terms_each_Subdirectory()
	{
		 int num_of_words_subdirectory=0;
		 Set set = SubDirectorytokens.entrySet();
	        Iterator it = set.iterator();   
	        while(it.hasNext()) 
	        {
	        	Map.Entry me = (Map.Entry)it.next();
	        	int total_words_subdir=0;
	        	temp1= new TreeMap<String, Integer>() ;
	        	temp1=SubDirectorytokens.get(me.getKey());
	        	Set set2 = temp1.entrySet();
		        Iterator it2 = set2.iterator();
		        while(it2.hasNext()) 
		        {
		        	Map.Entry me2 = (Map.Entry)it2.next();
		        	total_words_subdir=total_words_subdir+Integer.parseInt(me2.getValue().toString());
		        }
		        temp_likelihood = new TreeMap<String, Double>();
		       for(int i=0;i<uniquewords_full_dir;i++)
		        {
		    	  int numOfSpecificWordsInSubDir=0;
		       	  if(temp1.containsKey(complete_tokens.get(i)))
		       		numOfSpecificWordsInSubDir=temp1.get(complete_tokens.get(i));
		       	  double lh=(double)(numOfSpecificWordsInSubDir+1)/(total_words_subdir+uniquewords_full_dir);
		       	  lh=Math.log10(lh);
		       	  temp_likelihood.put(complete_tokens.get(i), lh);
		        }
		       likelihood_All_Terms_in_All_SubDirectories.put(me.getKey().toString(), temp_likelihood);
	        }
	}

		private static void Files(String filePath,String type) throws FileNotFoundException 
		{
			String filepath1=filePath;
			File file = new File(filePath);
			System.out.println(filepath1);
			 if(file.isDirectory())
		        {
			        File fList[] = file.listFiles();
			        if(type=="train")
			        	directoryCount_train=fList.length;
			        for (int j=0;j<fList.length;j++) 
			        {
			           if(fList[j].isDirectory())
			             {
			        	   System.out.println(fList[j].getAbsolutePath());
			        	   tokens_temp=  new TreeMap<String, Integer>();
			        	   readFiles(fList[j].getAbsolutePath(),j,type);
			        	   if(type=="train")
			        	   SubDirectorytokens.put(j+"", tokens_temp);
			        	   if(type=="train")
			        	   SubDirNames.add(j+"");   
			             }
			        }
		        }
		}
		
		private static void readFiles(String filePath,int j,String type) throws FileNotFoundException 
		{
			File file = new File(filePath);
			int dirNum=j;
			 if(file.isDirectory())
		        {
		        	File listOfFiles[] = file.listFiles();
		        	ActualSubDirCount.put(dirNum+"", listOfFiles.length);
		            for(int i=0; i<listOfFiles.length; i++)
		            {
		                if(listOfFiles[i].isFile() && listOfFiles[i].getName()!=".DS_Store")
		                {
		                    tot_filesCount++;
		                    tokeniseWords(listOfFiles[i],dirNum,type);
		                }
		            }
		        }
		}
		
		private static void tokeniseWords(File file,int dirNum, String type) throws FileNotFoundException 
		{
			if(file.getName()!=".DS_Store")
			{
				Scanner inputFile = new Scanner(file);
				filescount[dirNum]=filescount[dirNum]+1;
				temp = new TreeMap<String, Integer>();
				while (inputFile.hasNextLine()) 
				{
				  if (inputFile.nextLine().contains("Lines"))
				    break; 
		    }
			while (inputFile.hasNextLine()) 
			{
				String currentRow = inputFile.nextLine();
					StringTokenizer stringTokenizer = new StringTokenizer(currentRow);
					while (stringTokenizer.hasMoreTokens()) 
					{
						String currentToken = stringTokenizer.nextToken().toLowerCase();
						String currentModifiedToken = currentToken.replaceAll("[^\\w]", "");
						if (currentModifiedToken.equals(""))
							continue;
						else
						{
						//test data requires..tf_each_doc
							if(type=="test")
							{
								if(!temp.containsKey(currentModifiedToken))
									temp.put(currentModifiedToken, 1);
								else
									temp.put(currentModifiedToken, temp.get(currentModifiedToken)+1);
							}
						//storing subdirectory data in tokens..(terms,frequency)..train data
							if(type=="train")
							{
							if (tokens_temp.containsKey(currentModifiedToken))
							{
								tokens_temp.put(currentModifiedToken,
								tokens_temp.get(currentModifiedToken) + 1);
							}
							else
							{
								tokens_temp.put(currentModifiedToken, 1);
								if(!complete_tokens.contains(currentModifiedToken))
								complete_tokens.add(currentModifiedToken);
							}
						  }
					   }
				    }
			    }
			inputFile.close();
			if(type=="test")
			{
			tf_each_doc_test_data.put(file.getName(),temp);
			}
			}
      }
}

