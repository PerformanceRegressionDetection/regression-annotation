package org.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.processor.Regression.MeasurementUnit;
import org.processor.Regression.RegressionDirection;

public class RegressionData {
	String methodName;
	String className;
	int repetition;
	Regression.MeasurementUnit unit;
	double treshold;
	Regression.RegressionDirection dir;

	double measurementInMillis;

	public String getMethodName() {
		return methodName;
	}

	public String getClassName() {
		return className;
	}

	public int getRepetition() {
		return repetition;
	}

	public Regression.MeasurementUnit getMeasurementUnit() {
		return unit;
	}

	public double getTreshold() {
		return treshold;
	}

	public Regression.RegressionDirection getDir() {
		return dir;
	}

	public void setMeasurementInNanos(long meas) {
		measurementInMillis = meas;
	}

	public RegressionData(String data) {
		StringTokenizer tokenizer = new StringTokenizer(data, ",");
		methodName = tokenizer.nextToken();
		className = tokenizer.nextToken();
		repetition = Integer.parseInt(tokenizer.nextToken());
		unit = resolveMeasurementUnitEnum(tokenizer.nextToken());
		treshold = Double.parseDouble(tokenizer.nextToken());
		dir = resolveRegressionDirectionEnum(tokenizer.nextToken());
		if(tokenizer.hasMoreTokens()){
			measurementInMillis = Double.parseDouble(tokenizer.nextToken());
		}
	}
	
	public Regression.MeasurementUnit resolveMeasurementUnitEnum(String shortName){
		Regression.MeasurementUnit u = null;
		switch (shortName){
			case "ms": u = Regression.MeasurementUnit.MILLISECOND; break;
			case "s": u = Regression.MeasurementUnit.SECOND; break;
			case "m": u = Regression.MeasurementUnit.MINUTE; break;
			case "h": u = Regression.MeasurementUnit.HOUR; break;
			case "d": u = Regression.MeasurementUnit.DAY; break;
			
			case "B": u = Regression.MeasurementUnit.BYTE; break;
			case "KB": u = Regression.MeasurementUnit.KILOBYTE; break;
			case "MB": u = Regression.MeasurementUnit.MEGABYTE; break;
			case "GB": u = Regression.MeasurementUnit.GIGABYTE; break;
		}
		return u;
	}
	
	public Regression.RegressionDirection resolveRegressionDirectionEnum(String shortName){
		Regression.RegressionDirection d = null;
		switch (shortName){
			case "more": d = Regression.RegressionDirection.MORE; break;
			case "less": d = Regression.RegressionDirection.LESS; break;
		}
		return d;
	}

	public RegressionData(String mN, String pN, int rep) {
		methodName = mN;
		className = pN;
		repetition = rep;
	}

	public RegressionData(String mN, String pN, int rep, double meas) {
		methodName = mN;
		className = pN;
		repetition = rep;
		measurementInMillis = meas;
	}

	/**
	 * Parses a given file for RegressionData lines and gives them back in a Set
	 * 
	 * @param dataFile
	 *            Location of the CSV file with the RegressionData lines
	 * @return Set of Regression if the file exists otherwise null
	 */
	public static Set<RegressionData> getRegressionDataFromFile(File dataFile) {
		try {
			if (dataFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(dataFile));
				HashSet<RegressionData> lines = new HashSet<RegressionData>();
				String line;
				while ((line = br.readLine()) != null) {
					RegressionData rd = new RegressionData(line);
					lines.add(rd);
				}
				return lines;
			}
		} catch (Exception e) {
			System.out.println("getRegressionDataFromFile ERROR");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return new String(methodName + "," + className + "," + repetition);
	}

	// writes data without repetition -> only names and the measurement is
	// important in the measurement output
	public String toStringWithMeasurement() {
		return new String(methodName + "," + className + "," + measurementInMillis);
	}

	public String toStringAllWithRepetition(){
		return new String(        methodName
						  + "," + className
						  + "," + repetition
						  + "," + unit.getShortForm()
						  + "," + treshold
						  + "," + dir.getShortForm()
						  + "," + measurementInMillis
						  );
	}

	@Override
	public int hashCode() {
		int result = 1;
		final int prime = 31;

		result = prime * result + methodName.hashCode();
		result = prime * result + className.hashCode();
		// result = prime * result + repetition;
		return result;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if (object instanceof RegressionData) {
			RegressionData otherRegressionData = (RegressionData) object;
			result = (this.methodName.equals(otherRegressionData.methodName)
					&& this.className.equals(otherRegressionData.className));
		}
		return result;
	}
}
