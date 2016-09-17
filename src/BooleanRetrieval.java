import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.io.*;
import java.util.*;

public class BooleanRetrieval {

	HashMap<String, Set<Integer>> invIndex;
	int[][] docs;
	HashSet<String> vocab;
	HashMap<Integer, String> map; // int -> word
	HashMap<String, Integer> i_map; // inv word -> int map

	public BooleanRetrieval() throws Exception {
		// Initialize variables and Format the data using a pre-processing class
		// and set up variables
		invIndex = new HashMap<String, Set<Integer>>();
		DatasetFormatter formater = new DatasetFormatter();
		formater.textCorpusFormatter("./all.txt");
		docs = formater.getDocs();
		vocab = formater.getVocab();
		map = formater.getVocabMap();
		i_map = formater.getInvMap();
	}

	void createPostingList() {
		// Initialize the inverted index with a SortedSet (so that the later
		// additions become easy!)
		for (String s : vocab) {
			invIndex.put(s, new TreeSet<Integer>());
		}
		// for each doc
		for (int i = 0; i < docs.length; i++) {
			// for each word of that doc
			for (int j = 0; j < docs[i].length; j++) {
				// Get the actual word in position j of doc i
				String w = map.get(docs[i][j]);

				/*
				 * TO-DO: Get the existing posting list for this word w and add
				 * the new doc in the list. Keep in mind doc indices start from
				 * 1, we need to add 1 to the doc index , i
				 */
				Set<Integer> set_of_thisWord = invIndex.get(w);
				set_of_thisWord.add(i + 1);
				invIndex.put(w, set_of_thisWord);
			}

		}
	}

	Set<Integer> intersection(Set<Integer> a, Set<Integer> b) {
		/*
		 * First convert the posting lists from sorted set to something we can
		 * iterate easily using an index. I choose to use ArrayList<Integer>.
		 * Once can also use other enumerable.
		 */
		// result tree set
		TreeSet<Integer> result = new TreeSet<Integer>();

		// if any of the set is empty
		if (a == null || b == null) {
			result = null;
		} else {
			ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
			ArrayList<Integer> PostingList_b = new ArrayList<Integer>(b);

			// Set indices to iterate two lists. I use i, j
			int i = 0;
			int j = 0;

			// TO-DO: Implement the intersection algorithm here
			// Checking the size of the sets and running the intersection only
			// till least frequent word

			while (i != PostingList_a.size() && j != PostingList_b.size()) {
				if (PostingList_a.get(i) < PostingList_b.get(j)) {
					++i;
					continue;
				} else if (PostingList_a.get(i) > PostingList_b.get(j)) {
					++j;
					continue;
				} else {
					result.add(PostingList_a.get(i));
					++j;
					++i;
					continue;
				}
			}
		}
		return result;
	}

	Set<Integer> evaluateANDQuery(String a, String b) {
		return intersection(invIndex.get(a), invIndex.get(b));
	}

	Set<Integer> union(Set<Integer> a, Set<Integer> b) {
		/*
		 * This is very simple if we use Java Collections and its methods TO-DO:
		 * Figure out how to perform union?
		 */
		TreeSet<Integer> result = new TreeSet<Integer>();
		// Implement Union here
		// result.addAll(a);
		// result.addAll(b);

		// Other way to do it - but I have commented it
		// Because I used Collections
		// Another way to get union

		if (a == null && b == null) {
			result = null;
		} else if (a == null) {
			result.addAll(b);
		} else if (b == null) {
			result.addAll(a);
		} else {
			ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
			ArrayList<Integer> PostingList_b = new ArrayList<Integer>(b);
			for (int i = 0; i < PostingList_a.size(); i++) {
				result.add(PostingList_a.get(i));
			}
			for (int j = 0; j < PostingList_b.size(); j++) {
				result.add(PostingList_b.get(j));
			}
		}
		return result;
	}

	Set<Integer> evaluateORQuery(String a, String b) {
		return union(invIndex.get(a), invIndex.get(b));
	}

	Set<Integer> not(Set<Integer> a) {
		TreeSet<Integer> result = new TreeSet<Integer>();
		int total_docs = docs.length;
		//System.out.println(total_docs);
		/*
		 * Hint: NOT is very simple. I traverse the sorted posting list between
		 * i and i+1 index and add the other (NOT) terms in this posting list
		 * between these two pointers First convert the posting lists from
		 * sorted set to something we can iterate easily using an index. I
		 * choose to use ArrayList<Integer>. Once can also use other enumerable.
		 */
		if (a == null) {
			for (int k = 1; k <= total_docs; k++) {
				result.add(k);
			}
		} else {
			ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
			int first = PostingList_a.get(0);
			int second;
			for (int n = 1; n < first; n++) {
				result.add(n);
			}
			// TO-DO: Implement the not method using above idea or anything you
			// find better!
			for (int i = 0; i < PostingList_a.size() - 1; i++) {
				first = PostingList_a.get(i);
				second = PostingList_a.get(i + 1);
				for (int n = first + 1; n < second; n++) {
					result.add(n);
				}
			}

			int last = PostingList_a.get(PostingList_a.size() - 1);
			for (int n = last+1; n <= total_docs; n++) {
				result.add(n);
			}
		}
		//System.out.println(result);
		return result;
	}

	Set<Integer> evaluateNOTQuery(String a) {
		return not(invIndex.get(a));
	}

	Set<Integer> evaluateAND_NOTQuery(String a, String b) {
		return intersection(invIndex.get(a), not(invIndex.get(b)));
	}

	public static void main(String[] args) throws Exception {

		// Initialize parameters
		BooleanRetrieval model = new BooleanRetrieval();
		int argsLen = args.length;

		// Generate posting lists
		model.createPostingList();

		// Print the posting lists from the inverted index
		
		 /*System.out.println("\nPrinting posting list:"); 
		 for(String s :model.invIndex.keySet()){
			 System.out.println(s + " -> " + model.invIndex.get(s)); 
		 }*/
		 

		// Read the query type and words for running the Boolean query
		String boolQueryType = args[0].toUpperCase();
		String search_one = args[1].toLowerCase();
		String temp_word = args[argsLen - 2].toLowerCase();
		String search_two;
		if (temp_word.endsWith(")")) {
			int strLen = temp_word.length();
			search_two = temp_word.substring(0, strLen - 1);
		} else {
			search_two = temp_word;
		}

		// output file
		String outputFileName = "./" + args[argsLen - 1];

		// Create the output file
		File file = new File(outputFileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		if (boolQueryType.equals("PLIST")) {
			Set<Integer> PLIST_result = model.invIndex.get(search_one);
			if (PLIST_result == null) {
				bufferedWriter.write(search_one + " -> []");
			} else {
				bufferedWriter.write(search_one + " -> "
						+ PLIST_result);
			}
		}
		if (boolQueryType.equals("AND")) {
			Set<Integer> AND_result = model.evaluateANDQuery(search_one, search_two);
			if (AND_result == null) {
				bufferedWriter.write(search_one + " AND " + search_two
						+ " -> []");
			} else {
				bufferedWriter.write(search_one + " AND " + search_two + " -> "
						+ AND_result);
			}
		}
		if (boolQueryType.equals("OR")) {
			Set<Integer> OR_result = model.evaluateORQuery(search_one, search_two);
			if (OR_result == null) {
				bufferedWriter.write(search_one + " OR " + search_two
						+ " -> []");
			} else {
				bufferedWriter.write(search_one + " OR " + search_two + " -> "
						+ OR_result);
			}
		}
		if (boolQueryType.equals("AND-NOT")) {
			Set<Integer> ANDNOT_result = model.evaluateAND_NOTQuery(search_one, search_two);
			if (ANDNOT_result == null) {
				bufferedWriter.write(search_one + " AND (NOT " + search_two
						+ ") -> []");
			} else {
				bufferedWriter.write(search_one + " AND (NOT " + search_two
						+ ") -> "
						+ ANDNOT_result);
			}
		}

		bufferedWriter.close();

		// Print test cases

		/*
		 * System.out.println();
		 * 
		 * System.out.println("\nTesting AND queries \n");
		 * System.out.println("mouse AND keyboard: " +
		 * model.evaluateANDQuery("mouse", "keyboard"));
		 * System.out.println("mouse AND wifi: " +
		 * model.evaluateANDQuery("mouse", "wifi"));
		 * System.out.println("button AND keyboard " +
		 * model.evaluateANDQuery("button", "keyboard"));
		 * System.out.println("mouse AND scrolling " +
		 * model.evaluateANDQuery("mouse", "scrolling"));
		 * System.out.println("errors AND report: " +
		 * model.evaluateANDQuery("errors", "report"));
		 * 
		 * 
		 * System.out.println("\nTesting OR queries \n");
		 * System.out.println("wifi OR scroll: " + model.evaluateORQuery("wifi",
		 * "scroll")); System.out.println("youtube OR reported: " +
		 * model.evaluateORQuery("youtube", "reported"));
		 * System.out.println("errors OR report: " +
		 * model.evaluateORQuery("errors", "report"));
		 * System.out.println("hell OR movie: " + model.evaluateORQuery("hell",
		 * "movie"));
		 * 
		 * System.out.println("\nTesting AND_NOT queries \n");
		 * System.out.println("mouse AND (NOT scroll): " +
		 * model.evaluateAND_NOTQuery("mouse", "scroll"));
		 * System.out.println("scroll AND (NOT mouse): " +
		 * model.evaluateAND_NOTQuery("scroll", "mouse"));
		 * System.out.println("lenovo AND (NOT logitech): " +
		 * model.evaluateAND_NOTQuery("lenovo", "logitech"));
		 */
		
		/*System.out.println("Testing");
		Set<Integer> a = new TreeSet();
		a.add(12);a.add(57);a.add(134);a.add(578);a.add(1240);a.add(1247);
		System.out.println("Not of lousy -> "+ model.not(a));*/
		

	}

}