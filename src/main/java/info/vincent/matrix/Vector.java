package info.vincent.matrix;

import java.util.Map;

public interface Vector extends Map<Integer,Double>{
	void setQuick(int column, double value);
	int getLength();

}
