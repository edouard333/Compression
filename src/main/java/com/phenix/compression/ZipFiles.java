package com.phenix.compression;

import com.phenix.compression.exception.ZipCustomException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Gère les Zips.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public final class ZipFiles {

    /**
     * Vérifie que le Zip passe une batterie de test.<br>
     * Pour l'instant, on ne sait vérifier qu'un Zip qui prend des fichiers et
     * les met à la racine du Zip.<br>
     * Si le Zip a un souci, on retourne une erreur.
     *
     * @param fichier_zip Le fichier zip.
     * @param liste_fichier Les fichiers qu'il doit contenir.
     *
     * @throws ZipCustomException Le Zip a un souci.
     */
    public static void checkZip(File fichier_zip, List<File> liste_fichier) throws ZipCustomException {
        int nb_fichier_trouve = 0;

        try (ZipFile zipFile = new ZipFile(fichier_zip)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // Vérifie si c'est un fichier :
                if (!entry.isDirectory()) {
                    File fichier = getFileByName(liste_fichier, entry.getName());

                    if (fichier == null) {
                        throw new ZipCustomException("Le fichier '" + entry.getName() + "' est introuvable hors du Zip.");
                    } else {
                        nb_fichier_trouve++;
                    }

                    if (getCrcFromFile(fichier) != entry.getCrc()) {
                        throw new ZipCustomException("Le hash (checksum) de '" + entry.getName() + "' n'est pas le même que le fichier original.");
                    }
                }
            }

            // Vérifie qu'on trouve le même nombre de fichier dans le Zip que ceux qui doivent si trouver.
            // On ne dit pas ceux manquant.
            if (nb_fichier_trouve != liste_fichier.size()) {
                throw new ZipCustomException("Le Zip n'aurait pas tous les fichiers à envoyer (trouvé : " + nb_fichier_trouve + "/" + liste_fichier.size() + ").");
            }
        } catch (ZipException exception) {
            throw new ZipCustomException("Erreur avec le Zip (corrompu ?) : " + exception.getMessage());
        } catch (IOException exception) {
            throw new ZipCustomException(exception.getMessage());
        }
    }

    /**
     * Retourne le CRC d'un fichier.
     *
     * @param fichier Le fichier.
     * @return Le CRC. Si on veut la représentation standard :
     * {@code String.format("%02x", crc).toUpperCase()}.
     *
     * @throws IOException
     */
    private static long getCrcFromFile(File fichier) throws IOException {
        byte[] data = Files.readAllBytes(fichier.toPath());
        Checksum checksum = new CRC32();
        checksum.update(data);
        return checksum.getValue();
    }

    /**
     * Retourne un fichier selon son nom de fichier de la liste.
     *
     * @param liste_fichier La liste de fichier.
     * @param nom_fichier Le nom de fichier.
     * @return Le fichier sinon {@code null}.
     */
    private static File getFileByName(List<File> liste_fichier, String nom_fichier) {
        for (File fichier : liste_fichier) {
            if (fichier.getName().equals(nom_fichier)) {
                return fichier;
            }
        }

        return null;
    }

    /**
     * Zipe une liste de fichier dans un ZIP.
     *
     * @param liste_fichier Liste des fichiers.
     * @param zip Le fichier ZIP de sortie.
     *
     * @throws ZipCustomException
     */
    public static void zipDirectory(ArrayList<File> liste_fichier, File zip) throws ZipCustomException {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            fos = new FileOutputStream(zip.getAbsolutePath());
            zos = new ZipOutputStream(fos);
            for (File file : liste_fichier) {
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1024 * 1024 * 30];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException exception) {
            try {
                if (zos != null) {
                    zos.close();
                }

                if (fos != null) {
                    fos.close();
                }
            } catch (IOException exception2) {
                throw new ZipCustomException(exception.getMessage() + ", et il n'a pas été possible de fermer le flux du Zip.");
            }

            exception.printStackTrace();
            throw new ZipCustomException(exception.getMessage());
        }
    }

    List<String> filesListInDir = new ArrayList<String>();

    /**
     * This method zips the directory
     *
     * @param dir
     * @param zipDirName
     */
    public void zipDirectory(File dir, File zipDirName) {
        try {
            populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                System.out.println("Zipping " + filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024 * 1024 * 30];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method populates all the files in a directory to a List
     *
     * @param dir
     * @throws IOException
     */
    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                filesListInDir.add(file.getAbsolutePath());
            } else {
                populateFilesList(file);
            }
        }
    }

    /**
     * This method compresses the single file to zip format
     *
     * @param file
     * @param zipFileName
     */
    private static void zipSingleFile(File file, String zipFileName) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 1024 * 30];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            //Close the zip entry to write to zip file
            zos.closeEntry();
            //Close resources
            zos.close();
            fis.close();
            fos.close();
            System.out.println(file.getCanonicalPath() + " is zipped to " + zipFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compression un fichier/dossier en GZIP.
     *
     * @param file Le fichier à compresser.
     * @param gzipFile Le fichier compressé.
     *
     * @throws IOException
     */
    public static void compressGzipFile(File file, File gzipFile) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(gzipFile);
        GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            gzipOS.write(buffer, 0, len);
        }
        // close resources
        gzipOS.close();
        fos.close();
        fis.close();
    }

    /**
     * Décompresser un fichier GZIP.
     *
     * @param gzipFile
     * @param newFile
     *
     * @throws IOException
     */
    public static void decompressGzipFile(File gzipFile, File newFile) throws IOException {
        FileInputStream fis = new FileInputStream(gzipFile);
        GZIPInputStream gis = new GZIPInputStream(fis);
        FileOutputStream fos = new FileOutputStream(newFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        // close resources
        fos.close();
        gis.close();
        fis.close();
    }
}
