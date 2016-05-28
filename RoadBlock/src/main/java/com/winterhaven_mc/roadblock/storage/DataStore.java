package com.winterhaven_mc.roadblock.storage;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Set;


public abstract class DataStore {

	private boolean initialized;
	
	DataStoreType type;

	String filename;

	/**
	 * Initialize storage
	 * @throws Exception
	 */
	abstract void initialize() throws Exception;
	
	/**
	 * Check that block at location is protected
	 * @param location block location to check for RoadBlock protection
	 * @return {@code true} if block at location is protected, otherwise {@code false}
	 */
	abstract boolean isProtected(final Location location);

	/**
	 * Store list of records
	 * @param locations a {@code Collection} of {@code Location}
	 * for block locations to be inserted into the datastore
	 */
	abstract void insertRecords(final Collection<Location> locations);

	/**
	 * Store record
	 * @param location a {@code Location} of a block to be inserted into the datastore
	 */
	abstract void insertRecord(final Location location);

	/**
	 * delete list of records
	 * @param locationSet {@code Collection} of {@code Location}
	 * containing unique composite keys of records to delete
	 */
	abstract void deleteRecords(final Collection<Location> locationSet);

	/**
	 * Delete record
	 * @param location unique composite key for a record
	 */
	abstract void deleteRecord(final Location location);

	/**
	 * get all records
	 * @return Set of {@code Location} for all block records
	 */
	abstract Set<Location> selectAllRecords();

	/**
	 * Get block records for locations within a chunk
	 * @param chunk the chunk containing records to be returned
	 * @return {@code Set} of {@code Location} for block records within the chunk
	 */
	abstract Set<Location> selectBlockLocationsInChunk(Chunk chunk);
	
	/**
	 * Close storage
	 */
	public abstract void close();

	/**
	 * Sync datastore to disk if supported
	 */
	abstract void sync();
	
	/**
	 * Delete datastore
	 */
	abstract boolean delete();
	
	/**
	 * Check that datastore exists
	 * @return boolean
	 */
	abstract boolean exists();
	
	/**
	 * Get datastore filename or equivalent
	 * @return filename of this datastore type
	 */
	String getFilename() {
		return this.filename;
	}

	/**
	 * Get datastore type
	 * @return DataStoreType of this datastore type
	 */
	DataStoreType getType() {
		return this.type;
	}
	
	/**
	 * Get datastore name
	 * @return display name of datastore
	 */
	String getDisplayName() {
		return this.getType().toString();
	}

	/**
	 * Get datastore name
	 * @return display name of datastore
	 */
	@Override
	public String toString() {
		return this.getType().toString();
	}

	/**
	 * Get datastore initialized field
	 * @return boolean
	 */
	boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * Set initialized field
	 * @param initialized boolean value to set field
	 */
	void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}

	
}
