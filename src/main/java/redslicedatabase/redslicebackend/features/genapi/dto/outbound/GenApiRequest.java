package redslicedatabase.redslicebackend.features.genapi.dto.outbound;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "modelUri"  // Укажите поле, которое будет определять тип модели
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChatGPT4oMiniRequest.class, name = "chatgpt4o-mini"),
        @JsonSubTypes.Type(value = DeepSeekV3Request.class, name = "deepseek-v3")
})
public interface GenApiRequest {
}
