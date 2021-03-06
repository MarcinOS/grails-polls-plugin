package org.grails.plugins.polls

import org.hibernate.FetchMode

class PollService {

	boolean transactional = true

	def createPoll(Poll poll, List<Answer> answers) {
		answers.each {poll.addToAnswers(it)}
		poll.save();
	}

	def createPoll(String question, List<String> answers) {
		Poll poll = new Poll(question: question, startDate: new Date());
		List<Answer> answerObjects = answers.collect{new Answer(content: it)};
        createPoll(poll, answerObjects);
	}

	def increaseVotes(Answer answer) {
		answer.votes += 1;
		answer.save();
	}

	def getLatestPoll() {
		def results = Poll.withCriteria {
			eq("active", true);
			order("startDate", "desc");
		}
		return results[0];
	}

    def List answerContents(Poll poll) {
        return poll.answers.collect {it.content};
    }

    def List answerVotes(Poll poll) {
        return poll.answers.collect {it.votes};
    }
}
