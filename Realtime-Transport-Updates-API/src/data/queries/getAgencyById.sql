SELECT agency_id
    , agency_name
    , agency_url
    , agency_timezone
    , agency_lang
FROM [dbo].[agency]
WHERE agency_id = @agencyId
