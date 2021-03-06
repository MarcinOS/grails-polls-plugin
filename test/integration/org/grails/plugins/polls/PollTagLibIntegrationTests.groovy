package org.grails.plugins.polls

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * @author Marcin Świerczyński
 * Date: 2010-04-29 @ 12:42:20
 */
class PollTagLibIntegrationTests extends GroovyTestCase {
    PollTagLib pollTagLib;
    ApplicationTagLib g;
    def pollService;

    protected void setUp() {
        pollTagLib = new PollTagLib();
        g = new ApplicationTagLib();
    }

    void testShouldHasProperAnswersAndSize() {
        createAndRetrievePoll('Question1', ['A1', 'A2']);
        String chartImgTag = pollTagLib.pollResults(type: 'p3', width: '250', height: '100');
        assertTrue(chartImgTag.contains('chl=A1|A2'));
        assertTrue(chartImgTag.contains('chs=250x100'));
    }

    void testShouldSeeChangedVotes() {
        Poll p1 = createAndRetrievePoll('Question1', ['A1', 'A2']);

        increaseVotes(p1, 10);
        String chartImgTag = pollTagLib.pollResults(type: 'p3', width: '250', height: '100');
        assertTrue(chartImgTag.contains('chd=t:10,10'));

        increaseVotes(p1,1);
        chartImgTag = pollTagLib.pollResults(type: 'p3', width: '250', height: '100');
        assertTrue(chartImgTag.contains('chd=t:11,11'));
        assertFalse(chartImgTag.contains('chd=t:10,10'));
    }

    void testShouldGetPollByIdOrDefaultId() {
        Poll p1 = createAndRetrievePoll('Question1', ['A11', 'A12']);        
        Poll p2 = createAndRetrievePoll('Question2', ['A21', 'A22']);

        String chartImgTag = pollTagLib.pollResults(id: p1.id, type: 'p3', width: '250', height: '100');
        assertTrue(chartImgTag.contains('chl=A11|A12'));


        chartImgTag = pollTagLib.pollResults(id: p2.id, type: 'p3', width: '250', height: '100');
        assertTrue(chartImgTag.contains('chl=A21|A22'));


        chartImgTag = pollTagLib.pollResults(type: 'p3', width: '250', height: '100');
        assertTrue(chartImgTag.contains('chl=A21|A22'));
    }

    void testShouldDisplayBarChart() {
        createAndRetrievePoll('Question1', ['A11', 'A12']);
        String chartImgTag = pollTagLib.pollResults(type: 'bar', width: '250', height: '100');
        assertTrue(chartImgTag.contains('cht=bvg'));
    }

    void testShouldChangeChartColor() {
        createAndRetrievePoll('Question1', ['A11', 'A12']);
        String chartImgTag = pollTagLib.pollResults(type: 'pie', width: '250', height: '100', colors: ['ff0000', '00ff00', '0000ff']);
        assertTrue(chartImgTag.contains('cht=p'));
        assertTrue(chartImgTag.contains('chco=ff0000,00ff00,0000ff'));
    }

    void testShouldDisplayPollForm() {
        Poll p = createAndRetrievePoll('Question1', ['A11', 'A12']);
        String pollFormAsHtml = pollTagLib.poll();

        def formAction = g.createLink(controller: 'pollPlugin', action: 'submit');
        assertTrue pollFormAsHtml.startsWith('<div class="poll" id="poll_' + p.id + '">');
        assertTrue pollFormAsHtml.contains('<form action="'+formAction+'" method="post"');
        assertTrue pollFormAsHtml.contains('<legend>' + p.question + '</legend>');

        p.answers.each {
            assertTrue pollFormAsHtml.contains('<label for="answer_'+it.id+'">'+it.content+'</label>');
            assertTrue pollFormAsHtml.contains('<input type="radio" name="id" id="answer_'+it.id+'" value="'+it.id+'" />');
        }

        def voteLabel = g.message(code: 'poll.plugin.vote.button');
        assertTrue pollFormAsHtml.contains('<input type="submit" value="'+voteLabel+'" />');
        assertTrue pollFormAsHtml.endsWith('</div>');
    }

    void testShouldUseCheckboxIfPollIsMultiple() {
        Poll p = createAndRetrievePoll('Question1', ['A11', 'A12']);
        p.isMultiple = true;
        p.save();

        String pollFormAsHtml = pollTagLib.poll();
        p.answers.each {
            assertTrue pollFormAsHtml.contains('<input type="checkbox" name="id" id="answer_'+it.id+'" value="'+it.id+'" />');
        }
    }

    private Poll increaseVotes(Poll poll, int votesCount) {
        poll.answers.each {it.votes += votesCount};
        poll.save();
        return poll;
    }

    private Poll createAndRetrievePoll(String question, List<String> answers) {
		pollService.createPoll(question, answers);
		Poll poll = Poll.findByQuestion(question);
		return poll;
	}

}
