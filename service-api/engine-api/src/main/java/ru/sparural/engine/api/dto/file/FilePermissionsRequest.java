/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.sparural.engine.api.dto.file;

import lombok.Data;

import java.util.UUID;

@Data
public class FilePermissionsRequest {
    private Long userId;
    private UUID fileId;
}
