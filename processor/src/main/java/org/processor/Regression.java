package org.processor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Regression {	
	enum MeasurementUnit{
		MILLISECOND("ms"), //for time measurement
		SECOND("s"),
		MINUTE("m"),
		HOUR("h"),
		DAY("d"),
		BYTE("B"), //for memory consumption measurement
		KILOBYTE("KB"),
		MEGABYTE("MB"),
		GIGABYTE("GB");
		
		private String shortForm;

		private MeasurementUnit(String shortForm) {
			this.shortForm = shortForm;
		}
		
		public String getShortForm() {
			return shortForm;
		}
	}
	
	enum RegressionDirection{
		MORE("more"),
		LESS("less");
		
		private String direction;

		private RegressionDirection(String shortForm) {
			this.direction = shortForm;
		}
		
		public String getShortForm() {
			return direction;
		}
	}
	
	int rep() default 10;
	MeasurementUnit unit() default MeasurementUnit.MILLISECOND;
	double treshold();
	RegressionDirection dir() default RegressionDirection.MORE;
}
