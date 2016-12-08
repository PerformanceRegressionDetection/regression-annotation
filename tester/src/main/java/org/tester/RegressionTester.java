package org.tester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.processor.RegressionData;

public class RegressionTester {

	private long startTime;
	private long stopTime;
	private long testTimeInMs;
	
	int howManyCount;

	private boolean testMethodsInvoked = false;
	private Set<RegressionData> allMethodsToMeasure;
	private Set<RegressionData> measuredRegressionData;
	
	private String measMethodPath;
	private String measDataPath;

	public RegressionTester(String measMethodPath, String measDataPath) {
		this.measMethodPath = measMethodPath;
		this.measDataPath = measDataPath;
	}

	/**
	 * Holding the measured time for the currently tested method
	 */
	private List<Double> measurements;

	public void startMeasureTime() {
		startTime = System.currentTimeMillis();
	}

	public void stopMeasureTime(Object caller) {
		stopTime = System.currentTimeMillis();
		testTimeInMs = stopTime - startTime;
		
		// getting know the caller's method name
		String callerMethodName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();

		// getting know the caller's class name
		String[] callerName = caller.getClass().toString().split(" ");
		String callerClassName = callerName[1];

		if (testMethodsInvoked) {
			howManyCount++;
			// making sure previous measurements doesn't count (e.g.: the one from the JUnit invocation)
			measurements.add((Double)(double)testTimeInMs);
			
			// calculating average of measurements
			Double sum = new Double(0), average = new Double(0);
			for (Double d : measurements) {
				sum += d;
			}
			average = sum / measurements.size();
			
			// deleting the line with equal method and classname
			// two RegressionData equals if they share the same method and
			// classname
			if (measuredRegressionData.contains(new RegressionData(callerMethodName, callerClassName, -1)))
				measuredRegressionData.remove(new RegressionData(callerMethodName, callerClassName, -1));

			// adding the lines to the measuredRegressionData
			// -1 indicates that here repetitionNum doesn't hold valuable
			// information
			RegressionData equalingLine = null;
			for(RegressionData rd : allMethodsToMeasure){
				if(rd.equals(new RegressionData(callerMethodName, callerClassName, -1))){
					equalingLine = rd;
				}
			}
			measuredRegressionData.add(new RegressionData(        callerMethodName
														  + "," + callerClassName
														  + "," + "-1" 
														  + "," + equalingLine.getMeasurementUnit().getShortForm()
														  + "," + equalingLine.getTreshold()
														  + "," + equalingLine.getDir().getShortForm()
														  + "," + average));

			// writing the current MeasurementData to file as it is complicated to
			// know when to end
			try {
				FileWriter writer = new FileWriter(measDataPath);
				for (RegressionData rd : measuredRegressionData) {
					writer.append(rd.toStringAllWithRepetition());
					writer.append(System.lineSeparator());
				}
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void invokeTestMethods(Object caller) {
		measuredRegressionData = new HashSet<RegressionData>();
		testMethodsInvoked = true;
		try {
			// get all the data of the functions from the file that are needed to run
			File f = new File(measMethodPath);
			allMethodsToMeasure = RegressionData.getRegressionDataFromFile(f);

			String[] callerName = caller.getClass().toString().split(" ");
			String callerClassName = callerName[1];
			// invoke the testcases
			for (RegressionData r : allMethodsToMeasure) {
				measurements = new ArrayList<Double>();
				String className = r.getClassName();
				String methodName = r.getMethodName();
				for (int i = 0; i < r.getRepetition(); ++i) {
					if (callerClassName.equals(className)) {
						Class<?> cls = Class.forName(className);
						Object o = cls.newInstance();

						Method setNameMethod = o.getClass().getMethod(methodName);

						setNameMethod.invoke(o);
						//System.out.println("Package: " + r.getClassName() + " Method: " + r.getMethodName() + " rep: "
								//+ r.getRepetition());
					}
				}
			}
		} catch (Exception e) {
			System.out.println("[ERROR] invokeTestMethods");
			e.printStackTrace();
		}
		testMethodsInvoked = false;
	}
}
