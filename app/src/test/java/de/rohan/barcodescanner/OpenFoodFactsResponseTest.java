package de.rohan.barcodescanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;

public class OpenFoodFactsResponseTest {

    @Test
    public void parsesProductResponse() throws Exception {
        String json = "{\"status\":\"success\",\"product\":{\"brands\":\"Example Brand\",\"product_name\":\"Example Product\",\"code\":\"1234567890123\",\"image_url\":\"https://example.com/product.jpg\"}}";

        JsonAdapter<OpenFoodFactsResponse> adapter = new Moshi.Builder()
                .build()
                .adapter(OpenFoodFactsResponse.class);

        OpenFoodFactsResponse response = adapter.fromJson(json);

        assertNotNull(response);
        assertNotNull(response.product);
        assertEquals("success", response.status);
        assertEquals("Example Brand", response.product.brands);
        assertEquals("Example Product", response.product.product_name);
        assertEquals("1234567890123", response.product.code);
    }
}
