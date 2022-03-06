package com.davies.naraka;

import com.davies.naraka.admin.domain.dto.system.UserQueryDTO;
import com.davies.naraka.admin.domain.enums.UserStatus;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.jackson.CustomBeanDeserializerModifier;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author davies
 * @date 2022/2/28 1:18 PM
 */
@Slf4j
public class PrintTest {


    @Test
    public void regx() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        System.out.println("AesEncryptorUtils.encrypt(\"138438383438\", \"dsadas$#@$@#\") = " + AesEncryptorUtils.encrypt("138438383438", "dsadas$#@$@#"));
    }

    @Test
    public void jacksonTest() throws JsonProcessingException {
        QueryField<Boolean> booleanQuery = new QueryField<>(QueryFilterType.EQUALS, true);
        QueryField<BigDecimal> bigDecimalQuery = new QueryField<>(QueryFilterType.EQUALS, new BigDecimal("3.66"));
        QueryField<QueryFilterType> enumQuery = new QueryField<>(QueryFilterType.EQUALS, QueryFilterType.GREATERTHANEQUAL);
        String enumQueryString = "{\"type\":\"EQUALS\",\"filter\":\"GREATERTHANEQUAL\"}";
        String bigDecimalQueryString = "{\"type\":\"EQUALS\",\"filter\":3.66}";
        String booleanQueryString = "{\"type\":\"EQUALS\",\"filter\":true}";
        String qaueryString = "{\"status\":[0],\"createdTime\":[\"2022-03-01 17:14:31\",\"2022-03-01 17:14:31\"]}";
        ObjectMapper objectMapper = objectMapper();
        UserQueryDTO query = new UserQueryDTO();
        query.setStatus(new QueryField<>(QueryFilterType.EQUALS, Lists.newArrayList(UserStatus.ENABLE)));
        query.setCreatedTime(Lists.newArrayList(new QueryField<>(QueryFilterType.LESSTHAN,LocalDateTime.now()),new QueryField<>(QueryFilterType.GREATERTHANEQUAL,LocalDateTime.now())));
         System.out.println(objectMapper.writeValueAsString(query));;
        UserQueryDTO queryField = objectMapper.readValue(qaueryString, UserQueryDTO.class);
        System.out.println("queryField = " + queryField);

    }

    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JavaTimeModule defaultModule = new JavaTimeModule();
        defaultModule.setDeserializerModifier(new CustomBeanDeserializerModifier(true));
        defaultModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        defaultModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        defaultModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        objectMapper.registerModule(defaultModule);
        return objectMapper;

    }




    public class QueryFieldDeserializer extends StdDeserializer<QueryField> {
        ObjectMapper objectMapper;
        protected QueryFieldDeserializer(Class<?> vc,ObjectMapper objectMapper) {
            super(vc);
            this.objectMapper = objectMapper;
        }

        @Override
        public QueryField deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.START_OBJECT) {
                return this.objectMapper.readValue(jp, QueryField.class);
            }
            if (t == JsonToken.VALUE_STRING) {
                return new QueryField(QueryFilterType.EQUALS, jp.getValueAsString());
            }
            return null;


        }
    }
}
