package com.sbspro.midProject.domain.attr.service;

import com.sbspro.midProject.domain.attr.entity.Attr;
import com.sbspro.midProject.domain.attr.repository.AttrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)

public class AttrService {
    private final AttrRepository attrRepository;

    //  조회
     public String get(String varName, String defaultValue){
         Attr attr = findAttr(varName);

         if (attr == null){
             return defaultValue;
         }

         if (attr.getExpireDate() != null && attr.getExpireDate().compareTo(LocalDateTime.now() ) < 0){
             return defaultValue;
         }

         return attr.getVal();
     }

    private Attr findAttr(String relTypeCode, Long relId, String typeCode, String type2Code){
        return attrRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2Code(relTypeCode, relId, typeCode, type2Code).orElse(null);
    }

    private Attr findAttr(String varName){
        String[] varNameBits = varName.split("__");
        String relTypeCode = varNameBits[0];
        long relId = Integer.parseInt(varNameBits[1]);
        String typeCode = varNameBits[2];
        String type2Code = varNameBits[3];

        return findAttr(relTypeCode, relId, typeCode, type2Code);
    }

    public long getAsLong(String varName, long defaultValue) {
        String value = get(varName, "");

        if (value.isBlank()) {
            return defaultValue;
        }

        return Long.parseLong(value);
    }

    public boolean getAsBoolean(String varName, boolean defaultValue) {
        String value = get(varName, "");

        if (value.isBlank()) {
            return defaultValue;
        }

        if (value.equals("true")) {
            return true;
        } else return value.equals("1");
    }

    public LocalDateTime getAsLocalDateTime(String varName, LocalDateTime defaultValue){

        String value = get(varName, "");

        if(value.isBlank()){
            return defaultValue;
        }

        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    }

    // 명령

    @Transactional
    public void set(String varName, String value){
        set(varName, value, null);
    }

    @Transactional
    public void set(String varName, String value, LocalDateTime expireDate){
        String[] varNameBits = varName.split("__");
        String relTypeCode = varNameBits[0];
        long relId = Long.parseLong(varNameBits[1]);
        String typeCode = varNameBits[2];
        String type2Code = varNameBits[3];

        set(relTypeCode, relId, typeCode, type2Code, value, expireDate);
    }

    @Transactional
    public void set(String varName, long value){
        set(varName, String.valueOf(value));
    }

    @Transactional
    public void set(String varName, long value, LocalDateTime expireDate){
        set(varName, String.valueOf(value), expireDate);
    }

    @Transactional
    public void set(String varName, boolean value){
        set(varName, String.valueOf(value));
    }

    @Transactional
    public void set(String varName, boolean value, LocalDateTime expireDate){
        set(varName, String.valueOf(value), expireDate);
    }

    @Transactional
    public void set(String relTypeCode, Long relId, String typeCode, String type2Code, LocalDateTime value, LocalDateTime expireDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        set(relTypeCode, relId, typeCode, type2Code, value.format(formatter), expireDate);
    }

    @Transactional
    public void set(String relTypeCode, Long relId, String typeCode, String type2Code, String value, LocalDateTime expireDate){
        Attr attr = findAttr(relTypeCode, relId, typeCode, type2Code);

        if(attr == null){
            attr = Attr
                    .builder()
                    .relTypeCode(relTypeCode)
                    .relId(relId)
                    .type2Code(type2Code)
                    .typeCode(typeCode)
                    .build();
        }

        attr.setVal(value);
        attr.setExpireDate(expireDate);

        attrRepository.save(attr);
    }
}
