package com.ticketlite.demo.structure.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.io.IOException;


public class PointDeserializer extends StdDeserializer<Point> {
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public PointDeserializer() {
        super(Point.class);
    }

    @Override
    public Point deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = parser.getCodec().readTree(parser);
        double latitude = node.get("latitude").asDouble();
        double longitude = node.get("longitude").asDouble();

        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
