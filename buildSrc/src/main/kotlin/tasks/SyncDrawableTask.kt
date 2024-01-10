package tasks

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Locale
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/*
 * Created by nvs266@gmail.com on 09/01/2024
 *
 *                                          &&&&&&&&&
 * “Any fool can write code               &&&
 *  that a computer can understand.      &&
 *  Good programmers write code         &  _____ ___________
 *  that humans can understand.”       II__|[] | |   I I   |
 *                -Martin Fowler-     |        |_|_  I I  _|
 *                                   < OO----OOO   OO---OO
 ************************************************************/
open class SyncDrawablesTask : DefaultTask() {

    companion object {

        private const val APPLICATION_NAME = "Google Drive API Java Quickstart"
        private const val DRIVE_FOLDER_ID = "your_folder_id"
        private const val SERVICE_ACCOUNT_ID = "your_service_account@utopian-pride-xxxx.iam.gserviceaccount.com"
        private const val SERVICE_ACCOUNT_PRIVATE_KEY_FILE_PATH = "your_p12_key_file_path"

        private const val DOWNLOADED_DIR_PATH = "build/ggdrive"
        private const val PROCESSED_SVG_DIR_PATH = "$DOWNLOADED_DIR_PATH/ProcessedSVG"
        private const val DRAWABLE_DIR_PATH = "app/src/main/res/drawable"
        private const val JAR_SVG_TO_VECTOR_ANDROID_PATH = "buildSrc/Svg2VectorAndroid-1.0.1.jar"
    }

    @TaskAction
    fun run() {
        downloadIconsFromDrive()
        convertSvgIconsToVectorAndroid()
        copyIconsIntoDesignSystemModule()
    }

    private fun downloadIconsFromDrive() {
        // Build a new authorized API client service.
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val credential = getCredentialsByServiceAccount(
            jsonFactory = jsonFactory,
            scopes = listOf(DriveScopes.DRIVE_READONLY),
            serviceAccountId = SERVICE_ACCOUNT_ID,
            serviceAccountPrivateKeyFilePath = SERVICE_ACCOUNT_PRIVATE_KEY_FILE_PATH
        )

        // Create Google Drive Service
        val drive = Drive
            .Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()

        // Download all SVG files from drive then put them into DOWNLOAD_DIR_PATH folder
        drive.getFilesInFolder(folderId = DRIVE_FOLDER_ID)
            .filter { it.mimeType == "image/svg+xml" }
            .takeIf { it.isNotEmpty() }
            ?.also {
                // create folder
                val tempFile = File(DOWNLOADED_DIR_PATH)
                if (!tempFile.exists()) {
                    tempFile.mkdirs()
                } else {
                    tempFile.deleteRecursively()
                    tempFile.mkdirs()
                }
            }
            ?.forEach { file ->
                // download file and save with name dc_ic_{file_name}
                val fileName = "dc_ic_" + file.name.toLowerCase(Locale.ROOT).replace(" ", "_").replace("-", "_")
                val outputStream: OutputStream = FileOutputStream("$DOWNLOADED_DIR_PATH/$fileName")
                drive.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                println("Downloaded ${file.name} (${file.id}) to $fileName")
            }
    }

    private fun convertSvgIconsToVectorAndroid() {
        val process = ProcessBuilder("java", "-jar", JAR_SVG_TO_VECTOR_ANDROID_PATH, DOWNLOADED_DIR_PATH).start()
        process.waitFor()
        val exitCode = process.exitValue()
        if (exitCode == 0) {
            // Because after converting SVG files to vector format, the file name will be appended with "_svg"
            // So we need to remove it
            val directory = File(PROCESSED_SVG_DIR_PATH)
            if (!directory.isDirectory) {
                throw RuntimeException("The specified path is not a directory: ${directory.absolutePath}")
            } else {
                directory.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        val newName = file.name.replace("_svg", "")
                        val newFile = File(directory, newName)
                        file.renameTo(newFile)
                    }
                }
            }
        } else {
            throw RuntimeException("Failed to run JAR file. Exit code: $exitCode")
        }
    }

    private fun copyIconsIntoDesignSystemModule() {
        val sourceDir = File(PROCESSED_SVG_DIR_PATH)
        val destinationDir = File(DRAWABLE_DIR_PATH)

        // Delete all recent drawable files
        destinationDir.deleteRecursively()

        // Copy all files from sourceDir to destinationDir
        sourceDir.walk().forEach { sourceFile ->
            val relativePath = sourceDir.toPath().relativize(sourceFile.toPath())
            val destinationFile = destinationDir.toPath().resolve(relativePath)
            destinationFile.parent.toFile().mkdirs()
            sourceFile.copyTo(destinationFile.toFile())
        }
    }

    private fun Drive.getFilesInFolder(folderId: String): List<com.google.api.services.drive.model.File> {
        val query = "'$folderId' in parents and trashed=false"
        val fileList: FileList = files()
            .list()
            .setPageSize(999) // TODO: handle pagination
            .setQ(query)
            .setFields("files(id, name, mimeType)")
            .execute()
        return fileList.files.orEmpty()
    }

    @Deprecated("GoogleCredential deprecated")
    private fun getCredentialsByServiceAccount(
        jsonFactory: JsonFactory,
        scopes: List<String>,
        serviceAccountId: String,
        serviceAccountPrivateKeyFilePath: String,
    ): GoogleCredential {
        return GoogleCredential.Builder()
            .setTransport(NetHttpTransport())
            .setJsonFactory(jsonFactory)
            .setServiceAccountId(serviceAccountId)
            .setServiceAccountScopes(scopes)
            .setServiceAccountPrivateKeyFromP12File(File(serviceAccountPrivateKeyFilePath))
            .build()
    }
}

//  private const val CREDENTIALS_FILE_PATH = "/credentials.json"
//        private const val TOKENS_DIRECTORY_PATH = "driveTokens"
//    private fun listing() {
//        // Build a new authorized API client service.
//        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
//        val service = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//            .setApplicationName(APPLICATION_NAME)
//            .build()
//
//        // Print the names and IDs for up to 10 files.
//        val result = service
//            .files()
//            .list()
//            .setPageSize(100)
//            .setFields("nextPageToken, files(id, name)")
//            .execute()
//        val files = result.files
//        if (files == null || files.isEmpty()) {
//            println("No files found.")
//        } else {
//            println("Files:")
//            for (file in files) {
//                printf("%s (%s)\n", file.name, file.id)
//            }
//        }
//    }
//
//    /**
//     * Creates an authorized Credential object.
//     *
//     * @param HTTP_TRANSPORT The network HTTP Transport.
//     * @return An authorized Credential object.
//     * @throws IOException If the credentials.json file cannot be found.
//     */
//    @Throws(IOException::class)
//    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
//        // Load client secrets.
//        val `in` = DriveQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
//            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
//        val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))
//
//        // Build flow and trigger user authorization request.
//        val flow = GoogleAuthorizationCodeFlow.Builder(
//            /* transport = */ HTTP_TRANSPORT,
//            /* jsonFactory = */ JSON_FACTORY,
//            /* clientSecrets = */ clientSecrets,
//            /* scopes = */ SCOPES
//        )
//            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
//            .setAccessType("online")
//            .build()
//        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
//        //returns an authorized Credential object.
//        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
//    }
