import Pagination, { paginationPropsType } from "./Pagination";

export default {
    title : 'Atoms/Pagination',
    component : Pagination,
    argTypes : {
        isDark : { controls : 'boolean' },
        maxPage: { controls : 'number' },
        currentPage : { controls : 'number' }
    }
}

export const PaginationTemplate = (args:paginationPropsType) => <Pagination {...args}/>
PaginationTemplate.args = {
    isDark : false,
    maxPage : 10,
    currentPage : 1,
    setCurrentPage : ()=>{},
}