# Compression
Librairie pour gérer des compressions Zip etc.

# Comment l'utiliser ?
Utiliser la classe :
```java
import com.phenix.compression.ZipFiles;
```

Exemple :
```java
import com.phenix.compression.ZipFiles;

void main(String[] args) {
    // Vérifier qu'une liste de fichier sont dans un Zip :
    List<File> listeFichier = new ArrayList<File>();
    
    ZipFiles.checkZip(new File("fichier_zip.zip"), listeFichier);
    // ...
}
```
