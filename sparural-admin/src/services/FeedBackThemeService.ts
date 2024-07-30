import api from "../utils/api";
import { Endpoints } from "../constants";
import { CreateFeedBackThemeType, FeedBackThemeType } from "../types";

import { Versions } from "../config";

export class FeedBackThemeService {
  /**
   * Request for get FeedBackTheme
   */
  static async getFeedBackTheme(data: {
    offset?: number;
    limit?: number;
  }): Promise<Array<FeedBackThemeType>> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.FEED_BACK_THEME}?${query.join("&")}`
    );
    return response.data.data;
  }
  /**
   * Request for get FeedBackTheme by id
   */
  static async getFeedBackThemenById(id: number): Promise<FeedBackThemeType> {
    const response = await api.get(`${Endpoints.FEED_BACK_THEME}/${id}`);
    return response.data.data;
  }
  /**
   * Request for update FeedBackTheme
   */
  static async updateFeedBackThemen(
    id: number,
    data: CreateFeedBackThemeType
  ): Promise<FeedBackThemeType> {
    const sendData = {
      data: data,
      version: Versions.FEEDBACKTHEME
    };
    const response = await api.put(
      `${Endpoints.FEED_BACK_THEME}/${id}`,
      sendData
    );
    return response.data.data;
  }
  /**
   * Request for create FeedBackTheme
   */
  static async createFeedBackThemen(
    data: CreateFeedBackThemeType
  ): Promise<FeedBackThemeType> {
    const sendData = {
      data: data,
      version: Versions.INFO_SCREEN
    };
    const response = await api.post(Endpoints.FEED_BACK_THEME, sendData);
    return response.data.data;
  }
  /**
   * Request for delete FeedBackTheme
   */
  static async deleteFeedBackThemen(id: number): Promise<boolean> {
    const response = await api.delete(`${Endpoints.FEED_BACK_THEME}/${id}`);
    return response.data.success;
  }
}
