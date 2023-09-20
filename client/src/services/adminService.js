import axios from "axios"
import { ROOT_URL } from "."

export const getReportService = async (authorization,page) => {
    try {
        const response = await axios.get(
            `${ROOT_URL}/videos/reports?page=${page}&size=10&sort=report-count`,
            {
                headers: {
                    Authorization: authorization
                }
            }
        );
        console.log(response.data)
        return {
            status: 'success',
            data: response.data,
        }
    } catch (err) {
        return {
            status: 'error',
            data: err.response.data.message,
        }
    }
}