# Compression
Librairie qui gère des compressions Zip etc.

# Comment l'utiliser ?
Utiliser la classe [`com.phenix.compression.ZipFiles`](src/main/java/com/phenix/compression/ZipFiles.java).
```java
// Vérifier qu'une liste de fichier sont dans un Zip :
ZipFiles.checkZip(new File("fichier_zip.zip"), new ArrayList<File>());
...
```

Pour plus d'information, consulter la **[JavaDoc du projet](target/site/apidocs/index.html)**.
