package core.test;

import core.example.poena.PoenaRequestService;
import core.example.poena.PoenaServiceException;

import java.io.IOException;

public class VelocityParserTest {

    static PoenaRequestService poenaService = new PoenaRequestService();

    public static void main(String[] args) throws IOException {
        try {

            System.out.println(poenaService.sendRequest("kbkCode"));
        } catch (PoenaServiceException e) {
            e.printStackTrace();
        }
    }
}
