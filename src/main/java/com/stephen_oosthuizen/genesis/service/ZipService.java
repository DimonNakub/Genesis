package com.stephen_oosthuizen.genesis.service;

import com.stephen_oosthuizen.genesis.exception.GenerationException;
import com.stephen_oosthuizen.genesis.model.ProjectFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service for creating ZIP archives of generated projects.
 * Uses Apache Commons Compress for ZIP generation.
 */
@Service
@Slf4j
public class ZipService {

    /**
     * Create a ZIP archive from a list of project files
     *
     * @param files List of files to include in the ZIP
     * @param projectName Name of the project (used as root directory)
     * @return ZIP file as byte array
     */
    public byte[] createZip(List<ProjectFile> files, String projectName) {
        log.info("Creating ZIP archive for project: {} ({} files)", projectName, files.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(baos)) {

            // Set compression level
            zipOut.setLevel(9); // Maximum compression

            // Add each file to the ZIP
            for (ProjectFile file : files) {
                String entryPath = projectName + "/" + file.getPath();
                log.debug("Adding file to ZIP: {}", entryPath);

                ZipArchiveEntry entry = new ZipArchiveEntry(entryPath);
                entry.setSize(file.getContent().getBytes(StandardCharsets.UTF_8).length);

                // Set executable permission for scripts
                if (file.isExecutable()) {
                    entry.setUnixMode(0755); // rwxr-xr-x
                } else {
                    entry.setUnixMode(0644); // rw-r--r--
                }

                zipOut.putArchiveEntry(entry);
                zipOut.write(file.getContent().getBytes(StandardCharsets.UTF_8));
                zipOut.closeArchiveEntry();
            }

            zipOut.finish();
            byte[] zipBytes = baos.toByteArray();

            log.info("ZIP archive created successfully: {} bytes ({} files)", zipBytes.length, files.size());
            return zipBytes;

        } catch (IOException e) {
            log.error("Failed to create ZIP archive", e);
            throw new GenerationException("Failed to create ZIP archive", e);
        }
    }
}
