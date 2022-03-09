package org.ga4gh.starterkit.wes.utils.runmanager.engine;

import java.nio.file.attribute.BasicFileAttributes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String absolutePath;
    private String relativePath;
    private BasicFileAttributes fileAttributes;
}
