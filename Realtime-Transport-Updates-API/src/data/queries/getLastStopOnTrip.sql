SELECT TOP(1) trip_id
	, stop_id
	, stop_sequence
FROM stop_times
WHERE trip_index = @trip_index
ORDER BY stop_sequence DESC