package kolchoz_engine.resource;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import kolchoz_engine.models.SimpleModel;

public class ObjLoader {

	public static SimpleModel loadObj(String filePath) throws IOException {
		File file = new File(filePath);
		String path = file.getParent();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String s;
		String comment = "";
		String mtlLib = "";

		while ((s = reader.readLine()) != null) {
			if (s.length() < 2)
				continue;
			else if (s.startsWith("#"))
				comment += s + "\n";
			else if (s.startsWith("mtllib")) {
				mtlLib = path + "/" + s.split("\\s+")[1];
				break;
			} else break;
		}
		List<float[]> vertices = new ArrayList<>();
		List<float[]> normals = new ArrayList<>();
		List<float[]> UVs = new ArrayList<>();
		
		List<int[]> polygons = new ArrayList<>();
		List<int[]> polyCounts = new ArrayList<>();
		
		Map<Integer, String> indices2mat = new TreeMap<>();
		int[] sizes = readData(reader, vertices, normals, UVs, polygons, polyCounts, indices2mat, 8192);
		List<Float> vertices2 = new ArrayList<>();
		List<Float> normals2 = new ArrayList<>();
		List<Float> UVs2 = new ArrayList<>();
		List<Integer> polygons2 = new ArrayList<>();
		List<Integer> polyCount2 = new ArrayList<>();
	//	readData2(reader, vertices2, normals2, UVs2, polygons2, polyCount2, indices2mat);
	//	System.out.println("Vertices = " + sizes[0]);
	//	System.out.println("normals = " + sizes[1]);
	//	System.out.println("UVs = " + sizes[2]);
	//	System.out.println("Polygons = " + sizes[3]);
	//	System.out.println("Vertices = " + vertices2.size());
	//	System.out.println("normals = " + normals2.size());
	//	System.out.println("UVs = " + UVs2.size());
	//	System.out.println("Polygons = " + polyCount2.size());
	//	System.out.println("P = " + polygons2.size());
		
		return null;
	}
	


	private static void readData2(BufferedReader reader, List<Float> vertices, List<Float> normals, List<Float> UVs, List<Integer> polygons, List<Integer> polyCount,
			Map<Integer, String> indices2Materials) throws IOException {
		int polygonCount = 0, vertCount = 0;;
		String line;
		while((line = reader.readLine()) != null) {
			if (line.isEmpty()) continue;
			String[] split = line.split("\\s+");// для отделения 1 или 2 пробелов
			switch (split[0]) {
			case "v":
				vertices.add(Float.parseFloat(split[1]));
				vertices.add(Float.parseFloat(split[2]));
				vertices.add(Float.parseFloat(split[3]));
				vertCount++;
				break;
			case "vn":
				normals.add(Float.parseFloat(split[1]));
				normals.add(Float.parseFloat(split[2]));
				normals.add(Float.parseFloat(split[3]));
				break;
			case "vt":
				UVs.add(Float.parseFloat(split[1]));
				UVs.add(Float.parseFloat(split[2]));
				break;
			case "usemtl":
				indices2Materials.put(polygonCount, split[1]);
				break;
			case "f":
				int siz = split.length - 1;
				polyCount.add(siz);
				polygonCount++;
				for (int i = 0; i < siz; i++) {
					String[] plg = split[1].split("/");
					polygons.add(getIndex(plg[0], vertCount));
					polygons.add(getIndex(plg[1], vertCount));
					polygons.add(getIndex(plg[2], vertCount));
				}
				break;
			}

		}
		
		
	}
	private static int[] readData(BufferedReader reader, List<float[]> vertices, List<float[]> normals,
			List<float[]> UVs, List<int[]> polygons, List<int[]> polyCounts, Map<Integer, String> indices2mat, int size) throws IOException {
		int vertCount = 0, normCount = 0, UVsCount = 0, polyCount = 0;
		int vC = 0, nC = 0, uvC = 0, pC = 0, pgC = 0;
		float[] vert = new float[size * 3];
		float[] norm = new float[size * 3];
		float[] uv = new float[size * 2];
		int[] polyLen = new int[size];
		int[] polyInd = new int[size * 3];
		vertices.add(vert);
		normals.add(norm);
		UVs.add(uv);
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) continue;
			String[] split = line.split("\\s+");// для отделения 1 или 2 пробелов
			switch (split[0]) {
			case "v":
				vert[vC * 3] = Float.parseFloat(split[1]);
				vert[vC * 3 + 1] = Float.parseFloat(split[2]);
				vert[vC * 3 + 2] = Float.parseFloat(split[3]);
				vC++;
				vertCount++;
				if(vC == size) {
					vert = new float[size * 3];
					vertices.add(vert);
					vC = 0;
				}
				break;
			case "vn":
				norm[nC * 3] = Float.parseFloat(split[1]);
				norm[nC * 3 + 1] = Float.parseFloat(split[2]);
				norm[nC * 3 + 2] = Float.parseFloat(split[3]);
				nC++;
				if(nC == size) {
					norm = new float[size * 3];
					normals.add(norm);
					normCount += nC;
					nC = 0;
				}
				break;
			case "vt":
				uv[uvC * 2] = Float.parseFloat(split[1]);
				uv[uvC * 2 + 1] = Float.parseFloat(split[2]);
				uvC++;
				if(uvC == size) {
					uv = new float[size * 2];
					UVs.add(uv);
					UVsCount+= uvC;
					uvC = 0;
				}
				break;
			case "o":
//				objectIndex = addStr(objects, split[1]);
				break;
			case "g":
//				groupIndex = addStr(groups, split[1]);
				break;
			case "usemtl":
				indices2mat.put(polyCount + pC, split[1]);
				break;
			case "f":
				int siz = split.length - 1;
				polyLen[pC++] = siz;
				if(pC == size) {
					polyLen = new int[size];
					polyCounts.add(polyLen);
					polyCount+= pC;
					pC = 0;
				}
				for (int i = 0; i < siz; i++) {
					String[] plg = split[1].split("/");
					polyInd[pgC * 3] = getIndex(plg[0], vertCount);
					polyInd[pgC * 3 + 1] = getIndex(plg[1], vertCount);
					polyInd[pgC * 3 + 2] = getIndex(plg[2], vertCount);
					pgC++;
					if(pgC == size) {
						polyInd = new int[size * 3];
						polygons.add(polyInd);
						pgC = 0;
					}
				}
				break;
				
			}
		}
		vertCount+= vC;
		normCount+= nC;
		UVsCount+= uvC;
		polyCount+= pC;
		return new int[]{vertCount, normCount, UVsCount, polyCount};
	}

	private static int getIndex(String string, int vertexCount) {
		if(string.isEmpty()) return -1;
		int i = Integer.parseInt(string);
		if(i < 0) i = vertexCount + i;
		else i--;
		return i;
	}

	
	
	
	
	
	
	
/*
 * некоторые заметки по делению на вертекс группы:
 * 1.блендер(или нет) деление метолом меток "o"
 * 2.хз что, деление метками usemtl
 * 3.3д макс деление метками "o" затем "g" при том при 1 о всегда 1 г
 * 4. мая 
 * 5. стандарт делим на объекты "o", затем объект делим на группы "g", usemtl пока хз и ставится после/перед г
 * 6. индексы вершин/нормалей/тк начинаются с 1, если они отрицательные то тогда считаем ее как кол-во вершин - индекс.
 * 7 . если в описании полигона нет какого-то артибута, то записать ноль на его место(это будет означать его отсутствие)
 * 8 в мтл параметр map_Bump может иметь параметры, для текстуры юрать последнюю строку
 * как это читать:
 * 1. пройтись по всему файлу вичитав все "v", "vn","vt","f",итд. с последующей записью их в массивы(листы)
 * 2. при встрече любого из разделителей "o", "g", "usemtl" добавить их в массив(мапу) вместе с текущим номером(в массиве) полигона
 * 3. пройтись по массивам индексов разделителей для определения их колличества->в 1 о, может быть несколько г.
 * 4. разбить полигоны на группы между разделителями и привязать материя. после создать меш из каждого куска
 */
}