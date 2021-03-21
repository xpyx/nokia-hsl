SELECT [t].[trip_id]
    , [t].[trip_index]
    , [t].[trip_headsign]
    , [t].[route_id]
    , [st].[stop_id]
    , [st].[stop_headsign]
    , [t].[service_id]
    , [t].[direction_id]
    , [st].[arrival_time]
    , [st].[arrival_timestamp]
    , [st].[departure_time]
    , [st].[departure_timestamp]
    , [st].[stop_sequence]
    , [t].[shape_id]
    , [st].[shape_dist_traveled]
FROM stop_times st, trips t
WHERE [st].[stop_index] = (
	SELECT stop_index FROM stops WHERE stop_id = @stopId
)
AND [st].[trip_index] = [t].[trip_index]
AND [t].[service_id] IN ( 
    SELECT [service_id]
    FROM calendar
    WHERE [start_date] <= @currentDate 
    AND [end_date] >= @currentDate 
    AND friday = 1
    UNION
    SELECT [service_id] FROM calendar_dates
    WHERE [date] = @currentDate AND [exception_type] = 1
    EXCEPT
    SELECT [service_id] FROM calendar_dates
    WHERE [date] = @currentDate AND [exception_type] = 2
)
AND [st].[departure_timestamp] >= @currentTimestampMinusNumMinutes
AND [st].[departure_timestamp] <= @currentTimestampPlusOneHour
AND [st].[pickup_type] = 0
ORDER BY [st].[departure_timestamp]