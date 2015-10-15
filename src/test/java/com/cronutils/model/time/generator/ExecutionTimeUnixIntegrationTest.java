package com.cronutils.model.time.generator;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.joda.time.DateTime;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class ExecutionTimeUnixIntegrationTest {

    /**
     * Issue #38: every 2 min schedule doesn't roll over to next hour
     */
    @Test
    public void testEveryTwoMinRollsOverHour(){
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        Cron cron = new CronParser(cronDefinition).parse("*/2 * * * *");
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        DateTime time = DateTime.parse("2015-09-05T13:56:00.000-07:00");
        time = time.toDateTime(DateTime.now().getZone());
        DateTime next = executionTime.nextExecution(time);
        DateTime shouldBeInNextHour = executionTime.nextExecution(next);

        assertEquals(next.plusMinutes(2), shouldBeInNextHour);
    }

    /**
     * Issue #45: last execution does not match expected date. Result is not in same timezone as reference date.
     */
    @Test
    public void testMondayWeekdayLastExecution(){
        String crontab = "* * * * 1";
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        CronParser parser = new CronParser(cronDefinition);
        Cron cron = parser.parse(crontab);
        DateTime date = DateTime.parse("2015-10-13T17:26:54.468-07:00");
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        assertEquals(DateTime.parse("2015-10-12T23:59:00.000-07:00"), executionTime.lastExecution(date));
    }

    /**
     * Issue #45: next execution does not match expected date. Result is not in same timezone as reference date.
     */
    @Test
    public void testMondayWeekdayNextExecution(){
        String crontab = "* * * * 1";
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        CronParser parser = new CronParser(cronDefinition);
        Cron cron = parser.parse(crontab);
        DateTime date = DateTime.parse("2015-10-13T17:26:54.468-07:00");
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        assertEquals(DateTime.parse("2015-10-19T00:00:00.000-07:00"), executionTime.nextExecution(date));
    }
}
