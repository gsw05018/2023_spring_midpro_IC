package com.sbspro.midproject.domain.genFile.service;

import com.sbspro.midproject.base.util.Ut;
import com.sbspro.midproject.domain.genFile.entity.GenFile;
import com.sbspro.midproject.domain.genFile.repositroy.GenFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenFileService {
    private final GenFileRepository genFileRepository;

    //  조회
    public Optional<GenFile> findGenFileBy(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo) {

        return genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(relTypeCode, relId, typeCode, type2Code, fileNo);

    }

    // 명령
    @Transactional
    public GenFile save(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, String sourceFile) {

        findGenFileBy(relTypeCode, relId, typeCode, type2Code, fileNo).ifPresent(genFile -> {
            Ut.file.remove(genFile.getFilePath());
            genFileRepository.delete(genFile);
        });

        String originFileName = Ut.file.getOriginFileName(sourceFile);
        String fileExt = Ut.file.getExt(originFileName);
        String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);
        long fileSize = new File(sourceFile).length();
        String fileDir = getCurrentDirName(relTypeCode);

        GenFile genFile = GenFile.builder()
                .relTypeCode(relTypeCode)
                .relId(relId)
                .typeCode(typeCode)
                .type2Code(type2Code)
                .fileExtTypeCode(fileExtTypeCode)
                .fileExtType2Code(fileExtType2Code)
                .originFileName(originFileName)
                .fileSize(fileSize)
                .fileNo(fileNo)
                .fileExt(fileExt)
                .fileDir(fileDir)
                .build();

        genFileRepository.save(genFile);

        File file = new File(genFile.getFilePath());

        file.getParentFile().mkdirs();

        Ut.file.moveFile(sourceFile, file);
        Ut.file.remove(sourceFile);

        return genFile;
    }

    private String getCurrentDirName(String relTypeCode) {
        return relTypeCode + "/" + Ut.date.getCurrentDateFormatted("yyyy_MM_dd");
    }
}