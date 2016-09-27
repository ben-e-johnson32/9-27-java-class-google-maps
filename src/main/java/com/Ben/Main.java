package com.Ben;

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static Scanner scanner = new Scanner(System.in);
    static Scanner numScanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException
    {
        // First read the keys.
        String elevationKey = null;
        String geocodingKey = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("key.txt")))
        {
            elevationKey = reader.readLine();
            System.out.println(elevationKey);
        }
        catch (IOException ioe)
        {
            System.out.println("No key file found, or could not read key. Make sure key.txt exists.");
            System.exit(-1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("geocoding-key.txt")))
        {
            geocodingKey = reader.readLine();
            System.out.println(geocodingKey);
        }
        catch (IOException ioe)
        {
            System.out.println("No key file found, or could not read key. Make sure geocoding-key.txt exists.");
            System.exit(-1);
        }

        // Set up contexts for the two APIs we need.
        GeoApiContext context = new GeoApiContext().setApiKey(elevationKey);
        GeoApiContext geocodingContext = new GeoApiContext().setApiKey(geocodingKey);

        // Ask the user for the name of the place, then get results from the geocoding api.
        System.out.print("Enter the name of a place to find its elevation: ");
        String place = scanner.nextLine();
        GeocodingResult[] geoRequest = null;
        try
        { geoRequest = new GeocodingApiRequest(geocodingContext).address(place).await(); }
        catch (Exception e)
        { System.out.println("Error."); }

        // Print all the results with a number next to them and ask the user which one they meant.
        System.out.println("Enter the number next to the place you're looking for.");

        for (int x = 0; x < geoRequest.length; x++)
        {
            System.out.println(x + ". " + geoRequest[x].formattedAddress);
        }

        System.out.print("Enter the number: ");
        int choice = numScanner.nextInt();

        // Get the LatLng from the place they chose.
        double userLat = geoRequest[choice].geometry.location.lat;
        double userLng = geoRequest[choice].geometry.location.lng;
        LatLng userLatLng = new LatLng(userLat, userLng);

        // Get the elevation result.
        ElevationResult[] results = null;
        try
        { results = ElevationApi.getByPoints(context, userLatLng).await(); }
        catch (Exception e)
        { System.out.println("Something went wrong."); }

        if (results.length >= 1)
        {
            ElevationResult elevation = results[0];
            System.out.println("Elevation is " + elevation.elevation + " meters.");
            System.out.println(String.format("Elevation is %.2f meters.", elevation.elevation));
        }

        scanner.close();
        numScanner.close();
    }
}
