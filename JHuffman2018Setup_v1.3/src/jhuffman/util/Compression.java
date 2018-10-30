package jhuffman.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jhuffman.ds.Node;
import jhuffman.util.files.BinaryConverter;

public class Compression {

	public static Map<Character, String> getMappedTree(String filename) {
		BitReader bitReader = new BitReader(filename);
		SortedList<Node> list = bitReader.mapCharToFreq();
		Node root = TreeUtil.makeTree(list);

		StringBuffer sb = new StringBuffer();
		TreeUtil ut = new TreeUtil(root);
		Map<Character, String> huffmanPerCharacter = new HashMap<Character, String>();

		// primera hoja
		Node x = ut.next(sb);

		while (x != null) {
			// armo hash map
			huffmanPerCharacter.put((char) x.getC(), sb.toString());
			// siguiente hoja
			x = ut.next(sb);
		}
		return huffmanPerCharacter;
	}

	// Devuelve el arbol huffman
	public static String getHuffmanHeader(Map<Character, String> huffmanTree) {
		String header = "";

		for (Character c : huffmanTree.keySet()) {
			header += c + String.valueOf(huffmanTree.get(c).length()) + huffmanTree.get(c);
		}
		return header;

	}

	public static void compressContent(Map<Character, String> huffmanTree, String filename) throws IOException {
		FileReader fr = new FileReader(filename);
		int i;
		
		while ((i = fr.read()) != -1) {
			 String code = huffmanTree.get((char) i);
			 byte[] bytes = new byte[4];
			 String buffer = "";
			 
			 //ir agregando bits de code al array de bytes, y cuando llega a 4 bytes (32 bits)
			 // guardo en buffer lo que me sobra, lo agrego al nuevo array de bytes y continuo
			 // leyendo los siguientes codigos (y asi sucesivamente)
			 // TODO hacer un buffer de nosecuantos bytes, ir guardando bits a medida que leo
			 // TODO cuando se llena el buffer lo escribo al archivo, luego lo vacio para seguir leyendo
		}
		fr.close();

	}

	public static String compressText(Map<Character, String> huffmanPerCharacter, String text) {
		StringBuilder compressedText = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			String huffed = huffmanPerCharacter.get(text.charAt(i));
			compressedText.append(huffed);
		}
		String builded = compressedText.toString();
		return BinaryConverter.binaryToString(builded).toString();
	}

	public static Integer getCodes(BitReader reader, Map<String, Character> output) {
		int c;
		
		while ((c = reader.readBit()) != ((int) '\n')) {
			int length = reader.readBit() - '0';
			String code = "";
			for (int i = 0; i < length; i++) {
				code += reader.readBit() - '0';
			}
			output.put(code, (char) c);
		}
		String largo ="";
		while ((c = reader.readBit()) != ((int) '\n')) {
			largo += (char) c;
			
		}
		return Integer.parseInt(largo);
	}

	public static String getDecompressedContent(Map<String, Character> codes, BitReader reader, Integer largo) {
		String output = "";
		int c;
		String code = "";

			while (output.length() < largo && !reader.eof(c = reader.trueReadBit())) {
				code += c;
				Character caracter = codes.get(code);
				if (caracter != null) {
					output += caracter;
					code = "";
					continue;
				}
		}
		return output;
	}
}
