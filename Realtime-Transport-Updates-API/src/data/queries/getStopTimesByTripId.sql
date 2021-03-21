SELECT trip_id
    , trip_index
    , arrival_time
    , arrival_timestamp
    , departure_time
    , departure_timestamp
    , stop_id
    , stop_index
    , stop_sequence
    , stop_headsign
    , pickup_type
    , drop_off_type
    , shape_dist_traveled
FROM [dbo].[stop_times]
WHERE trip_id = @tripId