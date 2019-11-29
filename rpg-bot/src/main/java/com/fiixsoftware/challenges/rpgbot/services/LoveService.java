package com.fiixsoftware.challenges.rpgbot.services;

import com.fiixsoftware.challenges.rpgbot.persistence.models.*;
import com.fiixsoftware.challenges.rpgbot.persistence.models.types.ActionType;
import com.fiixsoftware.challenges.rpgbot.persistence.models.types.GameEntityType;
import com.fiixsoftware.challenges.rpgbot.persistence.models.types.StatType;
import com.fiixsoftware.challenges.rpgbot.persistence.models.types.StatementType;
import com.fiixsoftware.challenges.rpgbot.persistence.repositories.AffectionRepository;
import com.fiixsoftware.challenges.rpgbot.persistence.repositories.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Love Service.
 */
@Service
public class LoveService
{
	@Autowired
	private AffectionRepository affectionRepository;

	@Autowired
	private RelationshipRepository relationshipRepository;

	@Autowired
	private InventoryService inventoryService;

	/**
	 * Flirt with an NPC using a flirtatious statement.
	 *
	 * @param provider the provider
	 * @param recipient the recipient
	 * @param flirtyStatement the flirty statement
	 * @return the affection of the recipient
	 */
	public Affection flirtWith(GameEntity provider, GameEntity recipient, Statement flirtyStatement)
	{
		if (recipient.getGameEntityType() != GameEntityType.NPC)
		{
			return null;
		}

		Affection affectionForProvider = null;
		for (Affection affection : recipient.getAffections())
		{
			if (affection.getEntityAffectionIsToward().equals(provider))
			{
				affectionForProvider = affection;
			}
		}

		if (affectionForProvider == null)
		{
			affectionForProvider = new Affection(recipient, provider);
		}

		// Note:
		// Flirting does not always result in increased affection in real life.
		// Please do not make unwanted moves toward others, and respect when people say no.
		if (flirtyStatement.getStatementType() == StatementType.FLIRT)
		{
			affectionForProvider.setAmountOfAffection(Math.min(affectionForProvider.getAmountOfAffection() + 5L, Affection.MAXIMUM_AFFECTION));
		}
		else
		{
			affectionForProvider.setAmountOfAffection(Math.max(affectionForProvider.getAmountOfAffection() - 5L, Affection.MINIMUM_AFFECTION));
		}

		affectionRepository.save(affectionForProvider);
		return affectionForProvider;
	}

	/**
	 * Give a gift to an NPC.
	 *
	 * @param provider the provider
	 * @param recipient the recipient
	 * @param gift the gift
	 * @return the affection of the recipient
	 */
	public Affection giveGiftTo(GameEntity provider, GameEntity recipient, GameEntity gift)
	{
		if (recipient.getGameEntityType() != GameEntityType.NPC)
		{
			return null;
		}

		if (!(gift.getGameEntityType() == GameEntityType.CONSUMABLE || gift.getGameEntityType() == GameEntityType.ITEM ||  gift.getGameEntityType() == GameEntityType.EQUIPMENT))
			return null;

		long value = 0;
		for (Stat stat : gift.getStats()) {
			if (stat.getStatType() == StatType.MONETARY_VALUE) {
				value = stat.getValue();
				break;
			}
		}
		long level = 0;
		for (Stat stat : recipient.getStats()) {
			if (stat.getStatType() == StatType.LEVEL) {
				level = stat.getValue();
				break;
			}
		}

		long affectionLevel = value - level;

		if (affectionLevel > Affection.MAXIMUM_AFFECTION || affectionLevel < Affection.MINIMUM_AFFECTION)
			return null;

		Affection affection = new Affection(provider, recipient);

		affection.setAmountOfAffection(affectionLevel);

		return affection;
	}




	/**
	 * Show physical affection to an NPC.
	 *
	 * @param provider the provider
	 * @param recipient the recipient
	 * @param action the action
	 * @return the affection of the recipient
	 */
	public Affection showPhysicalAffectionTo(GameEntity provider, GameEntity recipient, Action action)
	{

		if (recipient.getGameEntityType() != GameEntityType.NPC)
		{
			return null;
		}

		Affection affectionForProvider = null;
		long amountOfAffection = 0;
		for (Affection affection : recipient.getAffections())
		{
			if (affection.getEntityAffectionIsToward().equals(provider))
			{
				affectionForProvider = affection;
				amountOfAffection = affection.getAmountOfAffection();
			}
		}

		if (action.getActionType() == ActionType.HUG) {

			if(amountOfAffection < (Affection.MAXIMUM_AFFECTION - Affection.STARTING_AFFECTION)/2)
				return null;

		}
		else if (action.getActionType() == ActionType.CUDDLE) {
			if (amountOfAffection < 85)
				return null;

			for (Relationship relationship : relationshipRepository.findAll()) {
				if(!((relationship.getRelationshipMemberA().equals(provider) && relationship.getRelationshipMemberB().equals(recipient))
					|| (relationship.getRelationshipMemberA().equals(recipient) && relationship.getRelationshipMemberB().equals(provider))))
					return null;
			}

	} else
		return null;
	}

	/**
	 * Enter a relationship with an NPC.
	 *
	 * @param initiator the initiator
	 * @param target the target
	 * @return the new relationship
	 */
	public Relationship enterRelationshipWith(GameEntity initiator, GameEntity target)
	{
		return null;
	}

	/**
	 * Break up an existing relationship with an NPC.
	 *
	 * @param heartbreaker the heartbreaker
	 * @param heartbroken the heartbroken
	 * @return the success or failure of ending the relationship
	 */
	public boolean breakUpWith(GameEntity heartbreaker, GameEntity heartbroken)
	{
		return false;
	}
}
