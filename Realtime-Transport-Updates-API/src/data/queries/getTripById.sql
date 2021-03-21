SELECT trip_id
    , trip_index
    , route_id
    , service_id
    , trip_headsign
    , direction_id
    , shape_id
FROM [dbo].[trips]
WHERE trip_id = @tripId