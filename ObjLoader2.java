package kolchoz_engine.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import kolchoz_engine.main.Maina;
import kolchoz_engine.models.SimpleModel;
import kolchoz_engine.models.materials.Material;
import kolchoz_engine.models.materials.SimpleMaterial;
import kolchoz_engine.models.materials.TexturedMaterial;
import kolchoz_engine.models.mesh.Mesh;
import kolchoz_engine.models.mesh.MeshType;
import kolchoz_engine.models.mesh.SimpleMesh;
import kolchoz_engine.shaders.ShaderStorage;
import kolchoz_engine.shaders.SimpleShader;
import kolchoz_engine.util.math.vector.Vec2;
import kolchoz_engine.util.math.vector.Vec3;
import kolchoz_engine.util.math.vector.VectorMath;

public class ObjLoader2 {

	/*нано план по оптимизации
	 * 1 прочитать несколько жирных файлов подряд и для каждого замерить время заргузки и сложить его
	 * 2 сначала заменить Scanner на BufferedReader. + замерить как в 1
	 * 3 в цикле while меетода readMesh поиграться с StringBuffer или StringBuilder
	 * 3.1 сначало попробовать создавать новые объекты на моментах вроде line.split("\\s+");
	 * 3.2 учитывая что длинна строки +- одинаковая, а максимальная строка не сильно больше средней. Заменить строки на что-то по статичнее вроде char[n]
	 * 4 сделать замеры для 3 
	 * 5 попытаться выкинуть строки из switch
	 */
	
	static long timeParseFile;
	
	public static SimpleModel loadObj(String path) throws IOException {
		long t0 = System.currentTimeMillis();
		//reader.
		File file = new File(path);
		String directory = file.getParent();
		//Scanner scn = new Scanner(file);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String s = "";
		String comment = "";
		String mtlLib = "";
		while ((s = reader.readLine()) != null) {
			if (s.length() < 2)
				continue;
			else if (s.startsWith("#"))
				comment += s + "\n";
			else if (s.startsWith("mtllib")) {
				mtlLib = directory + "/" + s.split("\\s+")[1];
				break;
			} else break;
		}
		
		/*
		while (scn.hasNext()) {
			s = scn.nextLine();
			if (s.length() < 2)
				continue;
			else if (s.startsWith("#"))
				comment += s + "\n";
			else if (s.startsWith("mtllib")) {
				//mtlLib = findMtl(s, directory);
				mtlLib = directory + "\\" + s.split("\\s+")[1];;
				break;
			} else
				break;
		}
*/
		System.out.println(comment + "\nStrat load");
		System.out.println("mtlib " + mtlLib);
		long t1 = System.currentTimeMillis();
		Long tt = Maina.timeLoaded.get(0);
		tt+= t1 - t0;
		Maina.timeLoaded.set(0, tt);
		t0 = System.currentTimeMillis();
		HashMap<String, Material> materials = readMtl(mtlLib);
		t1 = System.currentTimeMillis();
		tt = Maina.timeLoaded.get(1);
		tt+= t1 - t0;
		Maina.timeLoaded.set(1, tt);
		Multimap<String, Mesh> meshes = readMesh(reader, materials);
		
		Mesh[] mh = new Mesh[meshes.size()];
		Material[] mt = new Material[meshes.size()];
		SimpleShader shaders[] = new SimpleShader[meshes.size()];
		
		int i = 0;
		for(String key: meshes.keySet()) {
			Collection<Mesh> mesh = meshes.get(key);
			for (Mesh mes : mesh) {
				mh[i] = mes;
				mt[i] = materials.get(key);
				shaders[i] = (mt[i] instanceof TexturedMaterial)? ShaderStorage.getTipaPhongTextured(): ShaderStorage.getTipaPhong();
				i++;
				mes.load();
			}
		}

		SimpleModel model = new SimpleModel(mh, mt, shaders);
		return model;
	}

	/* Туть начинается весь ужос. Или чтение файла Obj и складывание данных в коллекции 
	 * + вызов след метода. 
	 */
	private static Multimap<String, Mesh> readMesh(BufferedReader reader, HashMap<String, Material> materials) throws NumberFormatException, IOException {
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Float> normals = new ArrayList<Float>(); // xyz, xyz ... xyz
		ArrayList<Float> textures = new ArrayList<Float>();// UV, UV ... UV
		ArrayList<String> mtls = new ArrayList<>();
		
		ArrayList<Integer> currentIndexArray = new ArrayList<Integer>();// съест ли это сборщик мусора?
		ArrayList<ArrayList<Integer>> indicesArrays = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> numIndices = new ArrayList<Integer>();
		
		int vertexc = 0;
		int normalc = 0;
		int texturesc = 0;
		int facesc = 0;
		
//		int objectIndex = -1; // для будушего использования(вероятного)
//		int groupIndex = -1; // для будушего использования(вероятного)
		int mtlIndex = -1;
		
		int maxIndex = 0;
		long t0;

//		ArrayList<String> objects = new ArrayList<String>(16); // для будушего использования(вероятного)
//		ArrayList<String> groups = new ArrayList<String>(16);  // для будушего использования(вероятного)
		System.out.println("File read");
		String line ;
		t0 = System.currentTimeMillis();
		while ((line = reader.readLine()) != null)  {
			
			if (line.isEmpty())
				continue;
			String[] split = line.split("\\s+");// для отделения 1 или 2 пробелов
			switch (split[0]) {
			case "v":
				Vertex v = new Vertex(Float.parseFloat(split[1]),
						Float.parseFloat(split[2]),
						Float.parseFloat(split[3]), vertices.size());
				vertices.add(v);
				vertexc++;
				break;
			case "vn":
				normals.add(Float.parseFloat(split[1]));
				normals.add(Float.parseFloat(split[2]));
				normals.add(Float.parseFloat(split[3]));
				normalc++;
				break;
			case "vt":
				textures.add(Float.parseFloat(split[1]));
				textures.add(Float.parseFloat(split[2]));
				texturesc++;
				break;
			case "o":
//				objectIndex = addStr(objects, split[1]);
				break;
			case "g":
//				groupIndex = addStr(groups, split[1]);
				break;
			case "usemtl":
				mtlIndex = addStr(mtls, split[1]);
				currentIndexArray = new ArrayList<Integer>();
				indicesArrays.add(currentIndexArray);
				numIndices.add(mtlIndex);
				break;
			case "f":
				if(split.length - 1 == 3) { // triangle
					int[] ind = new int[9];
					for (int i = 0; i < 3; i++) {
						String[] fs = split[i + 1].split("/");
						int j = fs[0].isEmpty() ? 0 : Integer.parseInt(fs[0]);
						ind[i * 3] = j == 0? -1 : j < 0? vertexc + j : j - 1;
						if(ind[i * 3] > maxIndex) maxIndex = ind[i * 3];
						
						j = fs[1].isEmpty() ? 0 : Integer.parseInt(fs[1]);
						ind[i * 3 + 1] = j == 0? -1 : j < 0? texturesc + j : j - 1;
						
						j = fs[2].isEmpty() ? 0 : Integer.parseInt(fs[2]);
						ind[i * 3 + 2] = j == 0? -1 : j < 0? normalc + j : j - 1;
						
					}
					//vertex 1
					currentIndexArray.add(ind[0]); currentIndexArray.add(ind[1]); 
					currentIndexArray.add(ind[2]);
					//vertex 2
					currentIndexArray.add(ind[3]); currentIndexArray.add(ind[4]); 
					currentIndexArray.add(ind[5]);
					//vertex 3
					currentIndexArray.add(ind[6]); currentIndexArray.add(ind[7]); 
					currentIndexArray.add(ind[8]);
					
					facesc++;
				} else if(split.length - 1 == 4) { // quad
					int[] ind = new int[12];
					for (int i = 0; i < 4; i++) {
						String[] fs = split[i+1].split("/");
						int j = fs[0].isEmpty() ? 0 : Integer.parseInt(fs[0]);
						ind[i * 3] = j == 0? -1 : j < 0? vertexc + j : j - 1;
						j = fs[1].isEmpty() ? 0 : Integer.parseInt(fs[1]);
						ind[i * 3 + 1] = j == 0? -1 : j < 0? texturesc + j : j - 1;
						j = fs[2].isEmpty() ? 0 : Integer.parseInt(fs[2]);
						ind[i * 3 +2] = j == 0? -1 : j < 0? normalc + j : j - 1;
					}
					
					currentIndexArray.add(ind[3]); currentIndexArray.add(ind[4]); currentIndexArray.add(ind[5]);// vertex 2 -> v1 tr1
					currentIndexArray.add(ind[9]); currentIndexArray.add(ind[10]); currentIndexArray.add(ind[11]);// vertex 4 -> v2  tr1
					currentIndexArray.add(ind[0]); currentIndexArray.add(ind[1]); currentIndexArray.add(ind[2]);// vertex 1 -> v3 tr1
					
					currentIndexArray.add(ind[3]); currentIndexArray.add(ind[4]); currentIndexArray.add(ind[5]);//   vertex 2 -> v1 tr2
					currentIndexArray.add(ind[6]); currentIndexArray.add(ind[7]); currentIndexArray.add(ind[8]);//   vertex 3 -> v2 tr2
					currentIndexArray.add(ind[9]); currentIndexArray.add(ind[10]); currentIndexArray.add(ind[11]);// vertex 4 -> v3  tr2
				
					facesc += 2;
				} else System.out.println("Mnogo uglov"); // n gone
				
				break;
				
			case "mtllib":
				break;
			default:
				for (String s : split) {
					System.out.print(s+" ");
				}
				System.out.println();
				break;
			}
		}
		long t1 = System.currentTimeMillis();
		Long tt = Maina.timeLoaded.get(2);
		tt+= t1 - t0;
		Maina.timeLoaded.set(2, tt);
		reader.close();
		System.out.println("Vertex = "+vertexc);
		System.out.println("Vnormals = "+normalc);
		System.out.println("Vtextcords = "+texturesc);
		System.out.println("Faces = "+facesc);
		System.out.println("Finded");
//		System.out.println("Objects = "+objects.size());
//		System.out.println("Groups = "+groups.size());
		System.out.println("Materials = "+mtls.size());
		System.out.println("End file readed");
		
		start = 0;
		end = vertexc;
		vcount = 0;
		dVcount = 0;
		outOfVCount = 0;
		Multimap<String, Mesh> meshes;
		meshes = processMeshes(vertices, normals, textures, indicesArrays, numIndices, mtls, materials);// вызов след метода
		
		System.out.println("Final vertex count  = " + vcount);
		System.out.println("Duplicate vertex = " + dVcount);
		System.out.println("Vertex in file = " + (vcount - dVcount - outOfVCount));
		System.out.println("Lishnih vertex = " + (vcount - dVcount - outOfVCount - vertexc));
		return meshes;
	}



	private static void calcTangent(Vertex v0, Vertex v1, Vertex v2, ArrayList<Float> textures) {
		
		Vec3 edge1 = new Vec3(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z);
		Vec3 edge2 = new Vec3(v2.x - v0.x, v2.y - v0.y, v2.z - v0.z);
		
		Vec2 uv0 = new Vec2(textures.get(v0.textureIndex * 2), textures.get(v0.textureIndex * 2 + 1));
		Vec2 uv1 = new Vec2(textures.get(v1.textureIndex * 2), textures.get(v1.textureIndex * 2 + 1));
		Vec2 uv2 = new Vec2(textures.get(v2.textureIndex * 2), textures.get(v2.textureIndex * 2 + 1));
		
		Vec2 deltaUV1 = new Vec2();
		Vec2 deltaUV2 = new Vec2();
		
        VectorMath.sub(deltaUV1, uv1, uv0);
        VectorMath.sub(deltaUV2, uv2, uv0);
        
        float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
        
        edge1.mul_(deltaUV2.y);
        edge2.mul_(deltaUV1.y);
        
        Vec3 tangent = new Vec3();
        VectorMath.sub(tangent, edge1, edge2);
        tangent.mul_(f);

    	v0.addTangent(tangent);
    	v1.addTangent(tangent);
    	v2.addTangent(tangent);
    	
	}




	/* Ужос начало, или вызов обработки данных*/
	private static Multimap<String, Mesh> processMeshes(ArrayList<Vertex> vertices, ArrayList<Float> normals,
			ArrayList<Float> textures, ArrayList<ArrayList<Integer>> indicesArray,
			ArrayList<Integer> numIndices, ArrayList<String> mtls, HashMap<String, Material> materials) {
		
		Multimap<String, Mesh> meshes = ArrayListMultimap.create();
		for(int i = 0; i < numIndices.size(); i++) {
			String mtl = mtls.get(numIndices.get(i));
			System.out.println(mtl);
			ArrayList<Integer> indices = indicesArray.get(i);
			Mesh mesh = processMesh(vertices, normals, textures, indices, materials.get(mtl) instanceof TexturedMaterial);
			meshes.put(mtl, mesh);
		}
		return meshes;
	}
	
	// для гипотетеской многопоточной загрузке спрятать если будет статик
	private static int start = 0; //начало куска индексов вершин.
	private static int end = 0;	// конец массива вершин до их обработки
	private static int vcount = 0; // финальное число вершин после обработки(создания дубликатов на месте шва)
	private static int dVcount = 0; // число тех самых дубликатов
	private static int outOfVCount = 0;// число вершин индексы которых вышли за пределы куска(но это не точно)
	
	/*
	start         offset                         end
	[vvvvvvvvvvvv|vvvvvvvvvvvvvvv|vvvvvvvvvvvvvvv|tttttttt]
	     -n-                                       -tail-
		 
		        start          offset                    end
	[vvvvvvvvvvvv|vvvvvvvvvvvvvvv|vvvvvvvvvvvvvvv|tttttttt|ttttttt]
	                    -n-                                 -tail-
	*/
	
	private static final int VADC = 11; // VERTEX ATTRIBS DATA COUNT params
	
	/**
	 * А вот и ужос подоспел. Или обработка данных о вершинах в треугольниках,
	 * и разгребание хаоса из обдж файлика(на много мегабайт).
	 * Вероятно ест много памяти. Ест процессорное время.
	 * @param vertices массив вершин(Класс Vertex лежит в конце этого класса.)
	 * @param normals Массив нормалей: XYZ, XYZ..XYZ
	 * @param textures Массив текстурных координат: UV, UV...UV
	 * @param indices Массив индексов вершин , текс. координат и нормалей: vXYZ, tUV, nXYZ...
	 * @param textured 
	 * @return Наконец-то Mesh.
	 */
	private static Mesh processMesh(ArrayList<Vertex> vertices, ArrayList<Float> normals,
							 ArrayList<Float> textures,  ArrayList<Integer> indices, boolean textured) {
		long t0 = System.currentTimeMillis();
		ArrayList<Integer> newIndices = new ArrayList<Integer>();
		Set<Integer> outOfStartVertices = new HashSet<>();
		end = vertices.size();// конец массива до обработки текущего куска, но после мледуещего
		int offset = 0; // начало след куска
		for(int i = 0; i < indices.size();) {
			int index = indices.get(i++);
			if(offset < index) offset = index;
			if(index < start) outOfStartVertices.add(index);
			Vertex v0 = processVertex(index, indices.get(i++), indices.get(i++), vertices, newIndices);
			
			index =  indices.get(i++);
			if(offset < index) offset = index;
			if(index < start) outOfStartVertices.add(index);
			Vertex v1 = processVertex(index, indices.get(i++), indices.get(i++), vertices, newIndices);
			
			index =  indices.get(i++);
			if(offset < index) offset = index;
			if(index < start) outOfStartVertices.add(index);
			Vertex v2 = processVertex(index, indices.get(i++), indices.get(i++), vertices, newIndices); 
			
			if(textured) calcTangent(v0, v1, v2, textures);
		}
		
		offset++; //ввиду того что он равен максимальному индексу, который равен size-1

		int errorV = 0;
		int n = offset - start;// число вершин в куске
		if(n < 0) n = 0;
		int tail = vertices.size() - end; // длинна хвоста появившегося после обработки(там лежат дубликаты)
		float[] vertexData = new float[(n + tail + outOfStartVertices.size()) * VADC]; // test. need uses offset
		vcount+= n + tail + outOfStartVertices.size();
		
		// запихивание вершин из куска
		for (int i = 0; i < n; i++) {// < n
			Vertex v = vertices.get(i + start);
			int nIndex = v.normIndex;
			int tIndex = v.textureIndex;
			if(!v.isInit()) {
				errorV++;
				continue;
			}
			vertexData[i * VADC] = v.x;
			vertexData[i * VADC + 1] = v.y;
			vertexData[i * VADC + 2] = v.z;

			vertexData[i * VADC + 3] = normals.get(nIndex * 3);
			vertexData[i * VADC + 4] = normals.get(nIndex * 3 + 1);
			vertexData[i * VADC + 5] = normals.get(nIndex * 3 + 2);
			if(textured) {
				vertexData[i * VADC + 6] = textures.get(tIndex * 2);
				vertexData[i * VADC + 7] =  textures.get(tIndex * 2 + 1);
				Vec3 tangent = v.getTangent();
				vertexData[i * VADC + 8] = tangent.x;
				vertexData[i * VADC + 9] = tangent.y;
				vertexData[i * VADC + 10] = tangent.z;
			}
			v.setIndex(-1, -1);
		}
		// запихивание вершин из хвоста
		for(int i = 0; i < tail; i++) {
			Vertex v = vertices.get(i + end);
			int nIndex = v.normIndex;
			int tIndex = v.textureIndex;
			
			int m = i + n;
			vertexData[m * VADC] = v.x;
			vertexData[m * VADC + 1] = v.y;
			vertexData[m * VADC + 2] = v.z;
			
			vertexData[m * VADC + 3] = normals.get(nIndex * 3);
			vertexData[m * VADC + 4] = normals.get(nIndex * 3 + 1);
			vertexData[m * VADC + 5] = normals.get(nIndex * 3 + 2);
			if(textured) {
				vertexData[m * VADC + 6] = textures.get(tIndex * 2);
				vertexData[m * VADC + 7] =  textures.get(tIndex * 2 + 1);
				Vec3 tangent = v.getTangent();
				vertexData[m * VADC + 8] = tangent.x;
				vertexData[m * VADC + 9] = tangent.y;
				vertexData[m * VADC + 10] = tangent.z;
			}
			

			v.setIndex(-1, -1);
		}
		HashMap<Integer, Integer> outOffIndices = new HashMap<Integer, Integer>();
		int m = n + tail;// колл-во вершин кусок + хвост
		//запихивание вершин чъи индексы оказались за пределами куска(ранше чем start)
		for (int i : outOfStartVertices) {
			Vertex v = vertices.get(i);
			int nIndex = v.normIndex;
			int tIndex = v.textureIndex;
			
			vertexData[m * VADC] = v.x;
			vertexData[m * VADC + 1] = v.y;
			vertexData[m * VADC + 2] = v.z;
			
			vertexData[m * VADC + 3] = normals.get(nIndex * 3);
			vertexData[m * VADC + 4] = normals.get(nIndex * 3 + 1);
			vertexData[m * VADC + 5] = normals.get(nIndex * 3 + 2);
			if(textured) {
				vertexData[m * VADC + 6] = textures.get(tIndex * 2);
				vertexData[m * VADC + 7] = textures.get(tIndex * 2 + 1);
				
				Vec3 tangent = v.getTangent();
				vertexData[m * VADC + 8] = tangent.x;
				vertexData[m * VADC + 9] = tangent.y;
				vertexData[m * VADC + 10] = tangent.z;
			}

						
			v.setIndex(-1, -1);
			outOffIndices.put(i, m);
			m++;
		}
		outOfVCount += outOfStartVertices.size();
		
		int[] indicesArray = new int[newIndices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			int index  = newIndices.get(i);
			if(index < start) 
				indicesArray[i] = outOffIndices.get(index);
			else 
				if(index < offset) indicesArray[i] = index - start;
				else 
					indicesArray[i] = index - end + n;	
		}

		start = offset;
		end = vertices.size();
		System.out.println("Indices " + indicesArray.length);
		float[] finalVertex = vertexData;
		long t1 = System.currentTimeMillis();
		Long tt = Maina.timeLoaded.get(3);
		tt+= t1 - t0;
		Maina.timeLoaded.set(3, tt);
/*		for(int i = 0; i < finalVertex.length; i+= VADC) {
			System.out.print("V = " + finalVertex[i] + " "+ finalVertex[i + 1] + " "+ finalVertex[i + 2] + " | ");
			System.out.print( finalVertex[i + 3] + " "+ finalVertex[i + 4] + " "+ finalVertex[i + 5] + " | ");
			System.out.print( finalVertex[i + 6] + " "+ finalVertex[i + 7] + " | ");
			System.out.print( finalVertex[i + 8] + " "+ finalVertex[i + 9] + " "+ finalVertex[i + 10] + "\n");
			
		}
		*/
		Mesh mesh = new SimpleMesh(MeshType.DEFAULT, vertexData, indicesArray);
		if(errorV > 0) {
			System.out.println("Read " + errorV + "Error vertex ");
		}
		
		return mesh;
	}
	
	/**
	 *  Тут происходит какая-то магия по обработке вершины, если она уже испльзовалась.
	 *  
	 * @param currVert Вершина
	 * @param texIndex Массив текст. координат UV...UV
	 * @param normalIndex Массив нормалей XYZ...XYZ
	 * @param vertices Массив вершин(@see Vertex)
	 * @param indices  массив Новых индексов, который потом загружается в VRAM.
	 * @return Обработанная вершина
	 */
	private static Vertex processProcessedVertex(Vertex currVert, int texIndex, int normalIndex, ArrayList<Vertex> vertices,
			ArrayList<Integer> indices) {
		if(currVert.textureIndex == texIndex) {
			indices.add(currVert.index);
			return currVert;
		}else {
			Vertex another = currVert.dublicate;
			if(another != null) {
				return processProcessedVertex(another, texIndex, normalIndex, vertices, indices);
			}else {
				another = new Vertex(currVert.x, currVert.y, currVert.z, vertices.size());
				another.setIndex(normalIndex, texIndex);
				currVert.dublicate = another;
				vertices.add(another);
				indices.add(another.index);
				dVcount++;
				return another;
			}
		}
		
	}


	/**
	 *  Обработка вершин.  Если вершина не использовалась, то установить ей текс. коорд. и нормали,
	 *  и добавить в массив индексов её индекс.
	 * @param index Индекс вершины
	 * @param texIndex Массив текст. координат UV...UV
	 * @param normalIndex Массив нормалей XYZ...XYZ
	 * @param vertices Массив вершин(@see Vertex)
	 * @param newIndices массив Новых индексов, который потом загружается в VRAM. 
	 * @return  Обработанная вершина
	 */
	private static Vertex processVertex(int index, int texIndex, int normalIndex, ArrayList<Vertex> vertices,
			ArrayList<Integer> newIndices) {
			Vertex v = vertices.get(index);
			if(v.isInit()) {
				return processProcessedVertex(v, texIndex, normalIndex, vertices, newIndices);
			}else {
				v.setIndex(normalIndex, texIndex);
				newIndices.add(index);
				return v;
			}
	}

	private static int addStr(ArrayList<String> arr, String s) {
		if(arr.contains(s)) return  arr.indexOf(s);
		else {
			arr.add(s);
			return  arr.size()-1;
		}
	}

	/*Чтение Mtl файла */
	private static HashMap<String, Material> readMtl(String path) throws FileNotFoundException {
		String currentMaterialName = "";
		ArrayList<String> names = new ArrayList<String>();

		// SimpleMaterial
		HashMap<String, float[]> Ka = new HashMap<String, float[]>();
		HashMap<String, float[]> Kd = new HashMap<String, float[]>();
		HashMap<String, float[]> Ks = new HashMap<String, float[]>();
		HashMap<String, float[]> Ke = new HashMap<String, float[]>();
		HashMap<String, float[]> Tf = new HashMap<String, float[]>();
		HashMap<String, Float> Ns = new HashMap<String, Float>();// string material name
		HashMap<String, Float> Ni = new HashMap<String, Float>();// string material name
		HashMap<String, Float> d = new HashMap<String, Float>();// string material name
		HashMap<String, Float> refl = new HashMap<String, Float>();// string material name
		HashMap<String, Float> ka = new HashMap<String, Float>();// string material name
		HashMap<String, Float> Tr = new HashMap<String, Float>();// string material name

		// textured material
		HashMap<String, String> map_Ka = new HashMap<String, String>();
		HashMap<String, String> map_Kd = new HashMap<String, String>();
		HashMap<String, String> map_Ks = new HashMap<String, String>();
		HashMap<String, String> map_Ke = new HashMap<String, String>();
		HashMap<String, String> map_Ns = new HashMap<String, String>();
		HashMap<String, String> map_refl = new HashMap<String, String>();
		HashMap<String, String> map_bump = new HashMap<String, String>();// or bump
		HashMap<String, String> disp = new HashMap<String, String>();// or bump

		File mtlFile = new File(path);
		Scanner scn = new Scanner(mtlFile);
		while (scn.hasNext()) {
			String s = scn.nextLine().trim();
			String[] ss = s.split("\\s+");
			switch (ss[0]) {
			case "#":
				for (String string : ss) {
					System.out.print(string + ' ');
				}
				System.out.println();
				
				break;
			case "newmtl":
				names.add(ss[1]);
				currentMaterialName = ss[1];
				break;
			case "Ns":
				Ns.put(currentMaterialName, Float.parseFloat(ss[1]));
				break;
			case "Ni":
				Ni.put(currentMaterialName, Float.parseFloat(ss[1]));
				break;
			case "d":
				d.put(currentMaterialName, Float.parseFloat(ss[1]));
				break;
			case "Tr":
				Tr.put(currentMaterialName, Float.parseFloat(ss[1]));
				break;
			case "Ka":
				Ka.put(currentMaterialName, parse3Float(ss));
				break;
			case "Kd":
				Kd.put(currentMaterialName, parse3Float(ss));
				break;
			case "Ks":
				Ks.put(currentMaterialName, parse3Float(ss));
				break;
			case "Ke":
				Ke.put(currentMaterialName, parse3Float(ss));
				break;
			case "Tf":
				Tf.put(currentMaterialName, parse3Float(ss));
				break;
			case "map_Ka":
				map_Ka.put(currentMaterialName, getTail(ss));
				break;
			case "map_Kd":
				map_Kd.put(currentMaterialName, getTail(ss));
				break;
			case "map_Ks":
				map_Ks.put(currentMaterialName, getTail(ss));
				break;
			case "map_Ke":
				map_Ke.put(currentMaterialName, getTail(ss));
				break;
			case "map_Bump":
			case "map_bump":
			case "bump":
				map_bump.put(currentMaterialName, getTail(ss));
				break;
			default:
				for (String string : ss) {
					System.out.print(string + ' ');
				}
				System.out.println("\nHZ chto");
				break;
			}
		}
		scn.close();
		HashMap<String, Material> materials = new HashMap<String, Material>();

		for (Iterator<String> iterator = names.iterator(); iterator.hasNext();) {
			String name = iterator.next();
			Material material = null;
			if (map_Kd.containsKey(name)) {
				System.out.println("Ambient " + map_Ka.get(name));
				System.out.println("Diffuse " + map_Kd.get(name));
				System.out.println("Specular " + map_Ks.get(name));
				System.out.println("Normal " + map_bump.get(name));
				System.out.println("reflect " + map_refl.get(name));

				material =  new TexturedMaterial(name, "/catg", map_Ka.get(name), map_Kd.get(name),
						map_Ks.get(name), map_bump.get(name), map_refl.get(name));
			} else {
				System.out.println("Ambient " + Ka.get(name));
				System.out.println("Diffuse " + Kd.get(name));
				System.out.println("Specular " + Ks.get(name));
				
				System.out.println("reflect " + refl.get(name));
				System.out.println(Tr);
				material = new SimpleMaterial(name, Ka.get(name),
						Kd.get(name),
						Ks.get(name),
						Ns.get(name),
						d.get(name),
						Tr.get(name)==null? 0 : Tr.get(name), 
						1);
			}

			materials.put(name, material);
		}

		return materials;
	}

	private static String getTail(String[] strings) {
		String ss = new String();
		for (int i = 1; i < strings.length; i++) {
			ss += strings[i];
		}
		if (ss.contains("\\")) {
			return ss.substring(ss.lastIndexOf('\\') + 1);
		}
		if (ss.contains("/")) {
			return ss.substring(ss.lastIndexOf('/') + 1);
		}
		return ss;
	}

	private static float[] parse3Float(String[] ss) {
		float[] f = new float[3];
		f[0] = Float.parseFloat(ss[1]);
		f[1] = Float.parseFloat(ss[2]);
		f[2] = Float.parseFloat(ss[3]);
		return f;
	}

	/*Доставание имени Mtl файла без учета пути*/
	private static String findMtl(String name, String path) {
		int c = path.lastIndexOf('/');
		String file = path.substring(0, c);
		return file + "/" + name.split("\\s+")[1];
	}
	
private static	class Vertex{
	    int index;
		float x, y, z;
		int normIndex = -1, textureIndex =-1;
		private Vec3 tangent = new Vec3();
		
		Vertex dublicate = null;
		
		public Vertex(float x, float y, float z, int i) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.index = i;
		}
		
		public boolean isInit() {
			return normIndex != -1 && textureIndex != -1;
		}
		
		public void setIndex(int norm, int text) {
			normIndex = norm;
			textureIndex = text;
		}
		
		public void addTangent(Vec3 tangent) {
			this.tangent.add_(tangent);
		}
		
		public Vec3 getTangent() {
			tangent.normalize();
			return tangent;
		}
	}

}
