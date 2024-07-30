package ru.sparural.file.dto.sourceparams;

import lombok.*;
import ru.sparural.file.dto.FileSourceParameters;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileSourceRemoteParams implements FileSourceParameters {

    private String url;
}
