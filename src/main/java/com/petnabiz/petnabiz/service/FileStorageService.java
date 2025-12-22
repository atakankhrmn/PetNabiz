package com.petnabiz.petnabiz.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Clinic başvurusu için belgeyi kaydeder
     * @param file PDF / PNG / JPG
     * @return kaydedilen dosyanın path'i
     */
    String storeClinicDocument(MultipartFile file);
}
