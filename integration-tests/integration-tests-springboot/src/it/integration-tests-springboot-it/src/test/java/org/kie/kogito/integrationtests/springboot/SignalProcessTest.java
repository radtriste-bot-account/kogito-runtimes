/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.integrationtests.springboot;

import org.springframework.boot.test.context.SpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = KogitoSpringbootApplication.class)
public class SignalProcessTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testProcessSignals() {
        String pid = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/greetings")
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("test", emptyOrNullString())
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .body("testvalue")
                .post("/greetings/{pid}/signalwithdata", pid)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/greetings/{pid}", pid)
                .then()
                .statusCode(200)
                .body("test", is("testvalue"));

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/greetings/{pid}/signalwithoutdata", pid)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/greetings/{pid}", pid)
                .then()
                .statusCode(204);
    }
}
