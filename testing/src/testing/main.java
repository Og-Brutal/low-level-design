package testing;

import java.awt.List;
import java.util.ArrayList;

import javax.naming.spi.DirStateFactory.Result;

public class main {
	public static ArrayList<String> getPrefixSuffixStem(String token) {

	    ArrayList<String> resultList = new ArrayList<>();
	    resultList.add(""); // prefix
	    resultList.add(""); // suffix
	    resultList.add(""); // stem

	    if (token == null || token.trim().isEmpty()) {
	        return resultList;
	    }

	    try {
	        List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(token.trim());

	        if (results != null && !results.isEmpty()) {

	            Result r = results.get(0);  // take best result

	            String prefix = r.getPrefix();
	            String suffix = r.getSuffix();
	            String stem   = r.getStem();

	            if (prefix == null || prefix.equals("#")) prefix = "";
	            if (suffix == null || suffix.equals("#")) suffix = "";
	            if (stem   == null || stem.equals("#"))   stem   = token.trim();

	            resultList.set(0, prefix);
	            resultList.set(1, suffix);
	            resultList.set(2, stem);
	        }

	    } catch (Exception e) {
	        System.err.println("Error in prefix/suffix extraction: " + e.getMessage());
	    }

	    return resultList;
	}

}
