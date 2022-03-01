//package com.davies.naraka.autoconfigure.jackson;
//
//import com.davies.naraka.cloud.common.domain.QueryField;
//import com.davies.naraka.cloud.common.enums.QueryFilterType;
//import com.fasterxml.jackson.core.*;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.node.*;
//import com.fasterxml.jackson.databind.type.SimpleType;
//import com.google.common.base.Strings;
//
//import java.io.IOException;
//
///**
// * @author davies
// * @date 2022/2/28 10:05 PM
// */
//public class QueryFieldDeserializer extends JsonDeserializer<QueryField<?>> {
//
//    private static final String QUERY_FIELD_TYPE = "type";
//    private static final String QUERY_FIELD_FILTER = "filter";
//
//    @Override
//    public QueryField<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
//        //TreeNode tn = jp.readValueAsTree();
//        String name = jp.getCurrentName();
//        JsonToken jsonToken =jp.currentToken();
//        if(JsonToken.START_OBJECT.equals(jsonToken)){
//            String fieldName = jp.getCurrentName();
//            jsonToken = jp.nextToken();
//            if (JsonToken.VALUE_EMBEDDED_OBJECT.equals(jsonToken)){
//
//            }else{
//
//            }
//        }else if (JsonToken.VALUE_STRING.equals(jsonToken)){
//
//        }
//        return null;
//    }
//
//    private QueryField<?> deserializeQueryField(TreeNode tn, String name) throws IOException {
//        if (tn instanceof ObjectNode) {
//            TreeNode typeNode = tn.get(QUERY_FIELD_TYPE);
//            if (typeNode instanceof TextNode) {
//                String type = ((TextNode) typeNode).asText();
//                return new QueryField<>(QueryFilterType.valueOf(type), filter(tn.get(QUERY_FIELD_FILTER), name));
//            }
//            throw new IllegalArgumentException(Strings.lenientFormat("deserialize:[%s] type error", name));
//        }
//        return new QueryField<>(QueryFilterType.EQUALS, filter(tn, name));
//    }
//
//    private Object filter(TreeNode tn, String name) throws IOException {
//        if (tn instanceof TextNode) {
//            return ((TextNode) tn).asText();
//        }
//        if (tn instanceof BooleanNode) {
//            return ((BooleanNode) tn).asBoolean();
//        }
//        if (tn instanceof DoubleNode) {
//            return ((DoubleNode) tn).asDouble();
//        }
//        if (tn instanceof LongNode) {
//            return ((LongNode) tn).asLong();
//        }
//        throw new IllegalArgumentException(Strings.lenientFormat("deserialize:[%s] value error", name));
//    }
//}
