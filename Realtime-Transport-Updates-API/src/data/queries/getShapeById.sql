SELECT shape_id
    , id
    , shape_pt_lat
    , shape_pt_lon
    , shape_pt_sequence
    , shape_dist_traveled
FROM [dbo].[shapes]
WHERE shape_id = @shapeId