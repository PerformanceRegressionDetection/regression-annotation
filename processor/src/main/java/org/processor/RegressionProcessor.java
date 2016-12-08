package org.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("org.processor.Regression")
public class RegressionProcessor extends AbstractProcessor{
	
	PrintWriter writer;
	File f;
	BufferedReader br;
	String strToCompare;
	
	
	/**
	 * Stores the RegressionData elements that are currently present in the file.
	 */
	HashSet<RegressionData> listFromFile;
	
	
	/**
	 * Stores the RegressionData elements that are processed during this round.
	 */
	HashSet<RegressionData> listFromAnnotations;
	
	
	/**
	 * Stores the merged list that will be the next round's listFromFile
	 */
	HashSet<RegressionData> outputList;
	
	private String measMethodPath;
	
	@Override
	public void init(ProcessingEnvironment processingEnv) {
		try {
			// initialising the data holders for this round
			listFromFile = new HashSet<RegressionData>();
			listFromAnnotations = new HashSet<RegressionData>();
			outputList = new HashSet<RegressionData>();
			
			//getting the temporary store path from the annotation processor parameter
			measMethodPath = processingEnv.getOptions().get("measMethodsPath");

			// filling the listFromFile if there are previous records
			f = new File(measMethodPath);
			if (f.exists()) {
				br = new BufferedReader(new FileReader(f));
				
				String line;
				while ((line = br.readLine()) != null) {
					RegressionData rd = new RegressionData(line);
					listFromFile.add(rd);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println("RegressionProcessor is processing the annotations");
		
		Set<? extends Element> ann = roundEnv.getElementsAnnotatedWith(Regression.class);

		try {
			writer = new PrintWriter(new PrintWriter(measMethodPath));

			for (Element e : ann) {
				Regression a = e.getAnnotation(Regression.class);
				// getting the values from the received annotation  =======\\
				Integer nor = new Integer(a.rep());		                // ||
				Regression.MeasurementUnit unit = a.unit();				// ||
				double treshold = a.treshold();							// ||
				Regression.RegressionDirection dir = a.dir();			// ||
				//=========================================================//

				RegressionData rd = new RegressionData(
						e.getSimpleName().toString()
						+ "," + e.getEnclosingElement().toString()
						+ "," + nor.toString()
						+ "," + unit.getShortForm()
						+ "," + treshold
						+ "," + dir.getShortForm()
						);

				listFromAnnotations.add(rd);
			}
			
			//adding annotations from the file : this retains the lines that previous rounds have added
			for (RegressionData rda : listFromAnnotations)
				outputList.add(rda);
					
			//adding the newly annotated methods
			for (RegressionData rda : listFromAnnotations) {
				boolean add = false;
				HashSet<RegressionData> rdfToWrite = new HashSet<RegressionData>();
				for (RegressionData rdf : listFromFile) {
					if (rdf.methodName.equals(rda.methodName) && rdf.getClassName().equals(rda.getClassName())) {
						add = false;
					} else {
						add = true;
						rdfToWrite.add(rdf);
					}
				}
				if (add)
					for (RegressionData rd : rdfToWrite)
						outputList.add(rd);
				//else
					//System.out.println("detected collision");
			}

			for (RegressionData output : outputList) {
				writer.println(output.toStringAllWithRepetition());
			}

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

}
